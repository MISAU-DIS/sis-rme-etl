# SIS-RME ETL

SIS-RME ETL is a configurable extraction, transformation and loading engine.
The current architecture separates the logical source model from the connector
that transports its records. An ETL item selects the endpoint through
`source.connector`; its primary data source describes the records and names
the physical table, file, route, queue or topic.

The previous database-oriented configuration remains supported and is
documented separately in [README-legacy.md](README-legacy.md).

## Configuration overview

An `EtlConfiguration` normally contains:

- a reusable `connectors` catalog;
- optional default source and destination connectors;
- one or more root `etlItemConfiguration` entries;
- one or more `operations`;
- optional data-model, template, validation and finalization settings.

```text
EtlConfiguration
  ├─ connectors[]
  ├─ operations[]
  └─ etlItemConfiguration[]
       ├─ source
       │    ├─ connector ──────────────> connector endpoint
       │    ├─ exactly one primary source
       │    │    ├─ tableDataSource ───> tableName
       │    │    ├─ jsonDataSource ────> name
       │    │    └─ csvDataSource ─────> name
       │    └─ extra*DataSource[]
       └─ dstConf[].resource ──────────> ConnectorRecordWriter
```

The primary source is inferred during initialization. There is no `primary`
property: exactly one of `tableDataSource`, `jsonDataSource` or
`csvDataSource` must be configured.

## Connectors

Connectors are declared once in `EtlConfiguration.connectors`. A connector is
not intrinsically a source or destination; the source or destination that
references it determines the required read or write capability.

```json
{
  "connectors": [
    {
      "id": "openmrs-source",
      "type": "JDBC",
      "uri": "jdbc:mysql://source-host/openmrs",
      "userName": "etl_reader",
      "password": "${source_password}",
      "properties": {
        "driverClassName": "com.mysql.cj.jdbc.Driver",
        "schema": "openmrs"
      }
    },
    {
      "id": "input-files",
      "type": "FILE",
      "uri": "/var/lib/sis-rme-etl/input"
    },
    {
      "id": "jsonl-output",
      "type": "FILE",
      "uri": "/var/lib/sis-rme-etl/output"
    }
  ],
  "defaultSourceConnector": "openmrs-source",
  "defaultDestinationConnector": "jsonl-output"
}
```

| Property | Meaning |
|---|---|
| `id` | Unique connector identifier. |
| `type` | `JDBC`, `FILE`, `REST`, `MESSAGE_QUEUE` or `CUSTOM`. |
| `uri` | Native endpoint address, such as a JDBC URL or filesystem root. |
| `userName` | Optional endpoint user name. |
| `password` | Optional endpoint password. |
| `properties` | Connector-specific properties. |

`REST`, `MESSAGE_QUEUE` and `CUSTOM` are extension points and require a
concrete reader or writer implementation before use.

### ETL state connection

The runtime currently persists its own processing state through a JDBC source
connection. This connection is independent from the connector that reads the
business records:

1. an explicit `srcConnInfo` supplies the state connection;
2. otherwise, a JDBC `defaultSourceConnector` supplies it;
3. if neither is available, or the default connector is not JDBC,
   `srcConnInfo` is required.

An item can therefore read JSON or CSV while ETL state remains in a database.

## The `source` configuration

`source` is the connector-neutral aggregate for all inputs of one ETL item. It
selects the connector and owns one inferred primary source, optional
additional sources and source processing policies.

### Connector and resource identity

```jsonc
"source": {
  "connector": "openmrs-source",
  "resource": {
    "name": "patient"
  },
  "tableDataSource": {
    "tableName": "patient"
  }
}
```

`source.connector` identifies the endpoint and `source.resource` identifies
what is read inside that endpoint. If the connector is omitted,
`defaultSourceConnector` is used; for a JDBC table, the legacy state connector
may also provide the endpoint. For JDBC sources, `resource` is optional because
the table name already provides a safe physical fallback. For non-JDBC sources,
it is required so that physical access remains separate from the logical data
model.

The primary data source `name` is the logical ETL data-source identity. The
physical file, route, queue, topic or table belongs to `resource.name`.

### JDBC table source

```json
{
  "configCode": "migrate-patients",
  "source": {
    "connector": "openmrs-source",
    "tableDataSource": {
      "tableName": "patient"
    }
  },
  "dstConf": [
    {
      "tableName": "patient",
      "resource": {
        "connector": "destination-db",
        "name": "patient"
      }
    }
  ]
}
```

`tableDataSource` contains table-specific extraction and relational metadata.
Its `tableName` is also the physical resource name supplied to the connector.

### JSON source

```json
{
  "configCode": "import-patient-events",
  "source": {
    "connector": "input-files",
    "resource": {
      "name": "events/patients.jsonl",
      "layout": "JSON_LINES",
      "recordPath": "/"
    },
    "jsonDataSource": {
      "name": "patientEvent",
      "fields": [
        {
          "name": "uuid",
          "dataType": "java.lang.String",
          "allowNull": false
        },
        {
          "name": "birthdate",
          "dataType": "java.util.Date"
        }
      ]
    }
  },
  "dstConf": [
    { "tableName": "patient" }
  ]
}
```

`resource.recordPath` uses JSON Pointer syntax to locate the records in the
physical content. `/` selects the root. `JSON_LINES`, `ARRAY` and `OBJECT`
describe the physical layout. The `jsonDataSource` describes only one logical
record; its `fields` list feeds the normal ETL `Field` model used by mappings
and transformations.

Related objects embedded in a JSON record are declared as
`extraJsonRecordDataSource`. They become ordinary auxiliary ETL source objects,
keeping the existing data-source and transformation model intact. For example,
given a document whose records are stored in `person_data` and contain an
`address` object:

```json
{
  "configCode": "import-person-data",
  "source": {
    "connector": "input-files",
    "resource": {
      "name": "person-data.json",
      "layout": "ARRAY",
      "recordPath": "/person_data"
    },
    "jsonDataSource": {
      "name": "person",
      "fields": [
        { "name": "id", "dataType": "java.lang.Integer", "allowNull": false },
        { "name": "full_name", "dataType": "java.lang.String" },
        { "name": "uuid", "dataType": "java.lang.String" }
      ],
      "extraJsonRecordDataSource": [
        {
          "name": "address",
          "path": "/address",
          "cardinality": "ONE",
          "required": false,
          "fields": [
            { "name": "id", "dataType": "java.lang.Integer" },
            { "name": "person_id", "dataType": "java.lang.Integer" },
            { "name": "full_address", "dataType": "java.lang.String" },
            { "name": "uuid", "dataType": "java.lang.String" }
          ]
        }
      ]
    }
  },
  "dstConf": [
    { "tableName": "person" }
  ]
}
```

Each extra source has its own flat field model and is attached to the primary
record through `auxLoadObject`. `ONE` requires an object at the relative path;
`MANY` requires an array and produces one auxiliary object per element.
Unknown properties and missing required fields are validated independently for
the primary and every extra data source.

### CSV source

```json
{
  "configCode": "import-patients-csv",
  "source": {
    "connector": "input-files",
    "resource": {
      "name": "patients.csv"
    },
    "csvDataSource": {
      "name": "patient",
      "delimiter": ";",
      "hasHeader": true,
      "charset": "UTF-8",
      "quote": "\"",
      "escape": "\\",
      "fields": [
        { "name": "uuid", "dataType": "java.lang.String", "allowNull": false },
        { "name": "gender", "dataType": "java.lang.String" }
      ]
    }
  },
  "dstConf": [
    { "tableName": "patient" }
  ]
}
```

The resource owns the physical file name. The CSV variant owns delimiter, header, charset,
quoting and field metadata. Its connector must provide a compatible reader.

### Additional data sources

Additional sources are direct children of `source` and enrich the current
transformation context without replacing the primary record:

```jsonc
"source": {
  "connector": "openmrs-source",
  "tableDataSource": {
    "tableName": "patient"
  },
  "extraTableDataSource": [
    { "tableName": "person" }
  ],
  "extraQueryDataSource": [
    {
      "name": "latestVisit",
      "query": "select encounter_id, patient_id from encounter"
    }
  ],
  "extraObjectDataSource": [
    { "name": "calculatedValues" }
  ],
  "extraJsonDataSource": [
    {
      "name": "demographics",
      "payload": "${demographics_payload}"
    }
  ]
}
```

`extraObjectDataSource` and `extraJsonDataSource` are connector-neutral and
can enrich JDBC, JSON or CSV records. Table and query additional sources
execute SQL and therefore require a JDBC primary source.

## Destination configuration

Destination transformation rules continue to be expressed by `dstConf`. Each
destination owns its resource so the logical destination and writer cannot
become misaligned:

```jsonc
"dstConf": [
  {
    "tableName": "patient_export",
    "resource": {
      "connector": "jsonl-output",
      "name": "daily/patients.jsonl"
    },
    "mapping": [
      { "srcField": "uuid", "dstField": "uuid" },
      { "srcField": "birthdate", "dstField": "birthdate" }
    ]
  }
]
```

JDBC remains the complete relational writer. The FILE writer produces UTF-8
JSON Lines, creates missing directories, confines paths to the connector root
and currently supports insert requests.

Detailed database mappings, parents and other configuration from the previous
surface are kept in [README-legacy.md](README-legacy.md).

## Validation rules

Validation occurs before processing. In particular:

- `source` must define exactly one primary variant;
- JSON and CSV sources require a non-empty, valid `fields` list;
- JSON and CSV primary sources require `name`;
- `source.resource` is rejected by the current configuration contract;
- connector IDs and primary resource names must resolve unambiguously;
- table/query additional sources require a JDBC primary source;
- connector-neutral additional sources accept any primary source;
- unknown JSON configuration properties are rejected.

## Operations and item selection

Operations may select root items through `etlItemConfigCodeWhiteList` and
`etlItemConfigCodeBlackList`. Manually assigned `configCode` values must be
unique. Child operations inherit omitted lists from their parent operation,
and references are validated before execution.

See the complete operation reference in
[README-legacy.md](README-legacy.md#the-operation-configuration).

## Data model

The optional `dataModel` configuration controls precompiled POJOs and static
schema metadata independently for source and destination models. It also owns
the additional classpath and POJO source and binary directories.

See the complete property reference in
[README-legacy.md](README-legacy.md#the-data-model-configuration).

## Architecture history

These documents record the connector migration by implementation phase. They
are historical design records; this README defines the current contract.

- [Phase 0 — baseline](docs/connector-architecture-phase-0.md)
- [Phase 1 — neutral contracts](docs/connector-architecture-phase-1.md)
- [Phase 2 — connector catalog and resources](docs/connector-architecture-phase-2.md)
- [Phase 3 — source reader](docs/connector-architecture-phase-3.md)
- [Phase 4 — destination writer](docs/connector-architecture-phase-4.md)
- [Phase 5 — processor migration](docs/connector-architecture-phase-5.md)
- [Phase 6 — first non-JDBC destination](docs/connector-architecture-phase-6.md)

## Build and execution

```bash
git clone https://github.com/csaude/openmrs-module-epts-etl.git
cd openmrs-module-epts-etl
mvn clean install -DskipTests
```

Run the standalone API JAR with an ETL configuration:

```bash
java -Dlog.level=INFO -jar sisrme-etl-api-1.0.jar "path/to/configuration/file.json"
```

Supported log levels include `DEBUG`, `INFO`, `WARN` and `ERR`. Runnable
examples are available in [docs/demo](docs/demo/README.md).
