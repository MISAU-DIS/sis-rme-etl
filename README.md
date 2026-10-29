# SIS-RME ETL

SIS-RME ETL is a configurable extraction, transformation and loading engine.
The current architecture separates the logical source model from the connector
that transports its records. An ETL item describes **what** its source records
look like under `source` and **where** they are obtained under
`source.resource`.

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
       │    ├─ resource ───────────────> ConnectorRecordReader
       │    ├─ exactly one primary source
       │    │    ├─ tableDataSource
       │    │    ├─ jsonDataSource
       │    │    └─ csvDataSource
       │    └─ extra*DataSource[]
       └─ dstConf[].resource ──────────> ConnectorRecordWriter
```

The primary source is inferred during initialization. There is no `primary`
property: exactly one of `tableDataSource`, `jsonDataSource` or
`csvDataSource` must be configured.

## Connectors

Connectors are declared once in `EtlConfiguration.connectors`. A connector is
not intrinsically a source or destination; the resource referencing it
determines the required read or write capability.

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
owns the resource, one inferred primary source, optional additional sources
and source processing policies.

### Source resource

```jsonc
"resource": {
  "connector": "openmrs-source",
  "name": "patient",
  "properties": {}
}
```

| Property | Meaning |
|---|---|
| `connector` | Connector ID. If omitted, `defaultSourceConnector` is used. |
| `name` | Connector-specific table, file, route, queue or topic name. |
| `properties` | Optional resource-specific settings. |

The resource belongs only to `source`; it must not be repeated inside a
primary source variant.

### JDBC table source

```json
{
  "configCode": "migrate-patients",
  "source": {
    "resource": {
      "connector": "openmrs-source",
      "name": "patient"
    },
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
Its `tableName` and the source resource `name` must identify the same table.

### JSON source

```json
{
  "configCode": "import-patient-events",
  "source": {
    "resource": {
      "connector": "input-files",
      "name": "events/patients.jsonl"
    },
    "jsonDataSource": {
      "layout": "JSON_LINES",
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

`recordPath` may select records inside an enclosing JSON object. The `fields`
list is mandatory and feeds the normal ETL `Field` model used by mappings and
transformations.

### CSV source

```json
{
  "configCode": "import-patients-csv",
  "source": {
    "resource": {
      "connector": "input-files",
      "name": "patients.csv"
    },
    "csvDataSource": {
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

The CSV variant owns delimiter, header, charset, quoting and field metadata.
Its connector must provide a compatible record reader.

### Additional data sources

Additional sources are direct children of `source` and enrich the current
transformation context without replacing the primary record:

```jsonc
"source": {
  "resource": {
    "connector": "openmrs-source",
    "name": "patient"
  },
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
- `source.resource` is required for non-JDBC primary sources;
- a resource must not be declared inside the primary variant;
- connector IDs and resource names must resolve unambiguously;
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
