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
       └─ destinations[]
            ├─ resource ───────────────> ConnectorRecordWriter
            └─ inferred/explicit physical adapter
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
  "destinations": [
    {
      "resource": {
        "connector": "destination-db",
        "name": "patient"
      },
      "tableDataDestination": {}
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
  "destinations": [
    {
      "resource": { "connector": "destination-db", "name": "patient" },
      "tableDataDestination": {}
    }
  ]
}
```

`resource.recordPath` uses JSON Pointer syntax to locate the records in the
physical content. `/` selects the root. `JSON_LINES`, `ARRAY` and `OBJECT`
describe the physical layout. The `jsonDataSource` describes only one logical
record; its `fields` list feeds the normal ETL `Field` model used by mappings
and transformations.

Related objects embedded in a JSON record are declared as
`extraJsonRecordDataSource` directly under `source`, alongside the other
`extra*DataSource` families. They become ordinary auxiliary ETL source objects,
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
      ]
    },
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
  },
  "destinations": [
    {
      "resource": { "connector": "destination-db", "name": "person" },
      "tableDataDestination": {}
    }
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
  "destinations": [
    {
      "resource": { "connector": "destination-db", "name": "patient" },
      "tableDataDestination": {}
    }
  ]
}
```

The resource owns the physical file name. The CSV variant owns delimiter, header, charset,
quoting and field metadata. Its connector must provide a compatible reader.

### Additional data sources

Additional sources are direct children of `source` and enrich the current
transformation context without replacing the primary record. Each additional
source may declare its own `resource`, including a `connector` from the root
`connectors` catalog:

```jsonc
"source": {
  "connector": "openmrs-source",
  "tableDataSource": {
    "tableName": "patient"
  },
  "extraTableDataSource": [
    {
      "tableName": "person",
      "resource": {
        "connector": "demographics-db",
        "name": "person"
      }
    }
  ],
  "extraQueryDataSource": [
    {
      "name": "latestVisit",
      "query": "select encounter_id, patient_id from encounter",
      "resource": {
        "connector": "clinical-reporting-db",
        "name": "latest-visit-query"
      }
    }
  ],
  "extraObjectDataSource": [
    {
      "name": "calculatedValues",
      "resource": {
        "connector": "rules-source",
        "name": "calculated-values"
      }
    }
  ],
  "extraJsonDataSource": [
    {
      "name": "demographics",
      "payload": "${demographics_payload}",
      "resource": {
        "connector": "event-source",
        "name": "demographics-payload"
      }
    }
  ]
}
```

The resource is resolved independently for each additional source:

- `resource.connector` selects an endpoint from `EtlConfiguration.connectors`;
- when `resource.connector` is omitted, the primary source connector is used;
- when `resource.name` is omitted, the additional source `name` is used;
- connector-neutral additional sources without `resource` inherit the complete
  primary resource;
- table and query additional sources without `resource` use their table/name on
  the primary connector.

For example, the following table remains on the primary connector but reads a
different physical resource:

```jsonc
"extraTableDataSource": [
  {
    "tableName": "person_address",
    "resource": {
      "name": "person_address"
    }
  }
]
```

`extraJsonRecordDataSource` normally omits `resource`: it describes a nested
record that is parsed together with, and therefore inherits the resource of,
the primary JSON record:

```jsonc
"extraJsonRecordDataSource": [
  {
    "name": "address",
    "path": "/address",
    "fields": [
      { "name": "id", "dataType": "java.lang.Integer" },
      { "name": "person_id", "dataType": "java.lang.Integer" }
    ]
  }
]
```

`extraObjectDataSource`, `extraJsonDataSource` and
`extraJsonRecordDataSource` are connector-neutral and can enrich JDBC, JSON or
CSV records. `extraTableDataSource` and `extraQueryDataSource` execute SQL, so
their resolved connector must be JDBC. It may be the primary connector or a
different JDBC connector declared specifically by the additional source.

## Destination configuration

The connector-neutral destination model is represented by
`EtlDestinationConfiguration`. It owns the transformation rules and logical
record model independently of JDBC:

- `resource` is the single configurable identity of the physical destination;
- `fields` and `primaryKey` describe the destination record;
- `mapping`, mapping policies, conditions and transformers remain at the
  destination root;
- `tableDataDestination`, `jsonDataDestination` and `csvDataDestination` add
  only technology-specific capabilities.

`resource.name` and `resource.connector` are canonical. Do not repeat them as
`destination.name` or `destination.connector`; those root properties are
rejected. `resource.properties` carries generic resource properties shared by
all destination technologies.

### JDBC destination

A JDBC adapter can be inferred entirely from the resolved connector:

```json
{
  "resource": {
    "connector": "destination-db",
    "name": "patient",
    "properties": {
      "schema": "openmrs"
    }
  },
  "primaryKey": {
    "fields": [
      { "name": "patient_id" }
    ]
  },
  "mapping": [
    { "srcField": "uuid", "dstField": "uuid" }
  ]
}
```

When `primaryKey` or `fields` are explicitly configured at the destination
root, they take precedence. Otherwise the JDBC adapter may discover fields,
PKs, unique keys, parents and children during physical metadata loading. A
discovered PK is promoted to the root destination model.

`tableDataDestination` remains available when the JDBC adapter must be selected
explicitly, but it does not own another resource:

```json
{
  "resource": {
    "connector": "destination-db",
    "name": "patient"
  },
  "tableDataDestination": {}
}
```

### File destinations

JSON and CSV formats can be inferred from `resource.properties.format` or the
resource filename extension. They may also be declared explicitly when
format-specific options are required:

```json
{
  "resource": {
    "connector": "file-output",
    "name": "daily/patients.csv",
    "properties": {
      "format": "csv"
    }
  },
  "fields": [
    { "name": "uuid", "dataType": "java.lang.String" },
    { "name": "birthdate", "dataType": "java.util.Date" }
  ],
  "csvDataDestination": {
    "delimiter": ";",
    "hasHeader": true,
    "charset": "UTF-8"
  }
}
```

Adapter resolution uses this precedence:

1. explicitly configured adapter;
2. `resource.properties.format`;
3. `.json`, `.jsonl` or `.csv` resource extension;
4. resolved connector type, notably JDBC.

At most one adapter may be active. An explicit adapter must be compatible with
the resolved connector. A destination without `resource` may still perform its
logical `fullLoad`—loading data sources, mappings and transformers—but it has
no physical writer.

`EtlItemConfiguration.destinations` is the executable destination contract.
The former `dstConf` property is no longer part of the current configuration
model; its historical format and detailed relational options remain documented
in [README-legacy.md](README-legacy.md). The destination architecture is
detailed in [Destination architecture — T1](docs/destination-architecture-t1-contracts.md).

## Validation rules

Validation occurs before processing. In particular:

- `source` must define exactly one primary variant;
- JSON and CSV sources require a non-empty, valid `fields` list;
- JSON and CSV primary sources require `name`;
- non-JDBC primary sources require `source.resource`;
- connector IDs and primary resource names must resolve unambiguously;
- table/query additional sources require their resolved connector to be JDBC;
- connector-neutral additional sources accept any primary source;
- the new destination model accepts at most one explicit adapter;
- `EtlDestinationConfiguration.resource` owns destination `name`, `connector`
  and generic properties;
- adapter-free, JSON and CSV destination models require root `fields`;
- an explicitly selected destination adapter must be compatible with its
  resolved connector;
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
