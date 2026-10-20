# SIS-RME ETL

The SIS-RME ETL module provides configurable extraction, transformation and
loading pipelines. Its runtime architecture is based on **connectors**: an ETL
item selects a source resource and one or more destination resources without
making the orchestration layer depend directly on JDBC connections.

The project continues to support the original database-oriented configuration.
Its complete reference is preserved in the
[legacy README](README-legacy.md). Existing configurations based on
`srcConnInfo`, `dstConnInfo`, `srcConf` and `dstConf` remain valid and are
adapted internally to JDBC connectors.

## Architecture

An ETL execution is represented by `EtlConfiguration`. It contains:

- an optional catalog of reusable connector endpoints;
- root `EtlItemConfiguration` items;
- one or more `EtlOperationConfig` operations;
- optional data-model, validation, finalization and template settings.

Each ETL item resolves its resources once during initialization:

```text
EtlConfiguration
  ├─ connectors[]
  ├─ operations[]
  └─ etlItemConfiguration[]
       ├─ srcConf.resource    ──> ConnectorRecordReader
       └─ dstConf[].resource  ──> ConnectorRecordWriters
```

The engine and `TaskProcessor` contracts receive a reader and a destination
writer aggregate. Native JDBC connections are confined to JDBC adapters and to
temporary compatibility paths used by transformations and processors that
have not yet been migrated.

## Connector configuration

Connectors are declared once at `EtlConfiguration.connectors`. A connector is
not intrinsically a source or destination; its use by `srcConf.resource` or
`dstConf[].resource` determines the direction and required capability.

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
      "id": "migration-database",
      "type": "JDBC",
      "uri": "jdbc:mysql://destination-host/migration",
      "userName": "etl_writer",
      "password": "${destination_password}"
    },
    {
      "id": "jsonl-output",
      "type": "FILE",
      "uri": "/var/lib/sis-rme-etl/output"
    }
  ],
  "defaultSourceConnector": "openmrs-source",
  "defaultDestinationConnector": "migration-database"
}
```

| Property | Meaning |
|---|---|
| `id` | Unique connector identifier. |
| `type` | `JDBC`, `FILE`, `REST`, `MESSAGE_QUEUE` or `CUSTOM`; defaults to `JDBC`. |
| `uri` | Native endpoint address, such as a JDBC URL or filesystem root. |
| `userName` | Optional endpoint user name. |
| `password` | Optional endpoint password. |
| `properties` | Connector-specific properties. |

`REST`, `MESSAGE_QUEUE` and `CUSTOM` are architectural extension points. They
require concrete reader or writer adapters before they can execute.

## ETL item resources

A resource identifies what is read or written. Its `connector` selects an
endpoint and its `name` identifies a table, file, route, queue, topic or another
endpoint-specific resource.

| Property | Meaning |
|---|---|
| `connector` | Optional connector ID. When omitted, selection uses the defaults described below. |
| `name` | Required resource name whenever `resource` is declared. |
| `properties` | Optional resource-specific settings. |

```json
{
  "configCode": "export-patients",
  "srcConf": {
    "tableName": "patient",
    "resource": {
      "connector": "openmrs-source",
      "name": "patient"
    }
  },
  "dstConf": [
    {
      "tableName": "patient_export",
      "resource": {
        "connector": "jsonl-output",
        "name": "patients.jsonl"
      }
    }
  ]
}
```

`srcConf` and `dstConf` remain the centre of the ETL item: they describe the
model, query, mapping and transformation rules. Their embedded `resource`
selects the I/O adapter. A JDBC resource paired with a table configuration must
use the same resource `name` and `tableName`.

Resources belong exclusively to `srcConf` and `dstConf`. There are no parallel
resource properties at `EtlItemConfiguration` level, so a destination and its
resource cannot become misaligned by position. The former `srcResource` and
`dstResources` properties are no longer accepted and cause configuration
deserialization to fail.

Resource selection follows this order:

1. the resource's explicit `connector`;
2. `defaultSourceConnector` or `defaultDestinationConnector`;
3. the internal legacy connector when only `srcConf`/`dstConf` is present;
4. the only explicitly configured connector, when selection is unambiguous.

Connector and resource references are validated before processing starts.

## Supported execution adapters

### JDBC source and destination

JDBC is the complete compatibility implementation. It supports reading,
inserts, updates, deletes, batching and the existing SQL-based extraction and
transformation behaviour.

Existing configurations do not need to declare connectors. The ETL creates
internal `legacy-src` and `legacy-dst` adapters from `srcConnInfo` and
`dstConnInfo` when `resource` is omitted from the corresponding `srcConf` or
`dstConf`. For mappings, parents, transformers, templates and the complete
database configuration, see the [legacy README](README-legacy.md).

### FILE destination

`FILE` is the first non-JDBC destination adapter. It writes JSON Lines (JSONL):
one compact JSON object per transformed record.

```json
{
  "connectors": [
    {
      "id": "openmrs-source",
      "type": "JDBC",
      "uri": "jdbc:mysql://source-host/openmrs",
      "userName": "etl_reader",
      "password": "${source_password}"
    },
    {
      "id": "jsonl-output",
      "type": "FILE",
      "uri": "/var/lib/sis-rme-etl/output"
    }
  ],
  "etlItemConfiguration": [
    {
      "srcConf": {
        "tableName": "patient",
        "resource": {
          "connector": "openmrs-source",
          "name": "patient"
        }
      },
      "dstConf": [
        {
          "tableName": "patient_export",
          "resource": {
            "connector": "jsonl-output",
            "name": "daily/patients.jsonl"
          }
        }
      ]
    }
  ]
}
```

The writer:

- creates missing directories and files;
- writes UTF-8;
- appends one JSON document per line;
- serializes ETL record fields rather than internal processing state;
- rejects resource paths outside the configured root;
- currently supports `INSERT` requests only.

FILE currently provides destination capabilities only. Extraction from files
requires a future reader adapter.

## Legacy compatibility

The previous configuration model remains supported:

```json
{
  "srcConnInfo": {
    "connectionURI": "jdbc:mysql://source-host/openmrs",
    "dataBaseUserName": "reader",
    "dataBaseUserPassword": "${source_password}"
  },
  "dstConnInfo": {
    "connectionURI": "jdbc:mysql://destination-host/migration",
    "dataBaseUserName": "writer",
    "dataBaseUserPassword": "${destination_password}"
  },
  "etlItemConfiguration": [
    {
      "srcConf": { "tableName": "patient" },
      "dstConf": [{ "tableName": "patient" }]
    }
  ]
}
```

No immediate migration is required. New configurations should declare the
connector catalog and embed resource references in `srcConf` and `dstConf` so
endpoint selection is no longer encoded only in database-specific fields.

For every legacy property and advanced ETL capability, consult
[README-legacy.md](README-legacy.md).

## Data model

The optional `dataModel` configuration controls precompiled POJOs and static
schema metadata independently for source and destination models. It also owns
the additional classpath and source/binary POJO directories.

See [The Data Model configuration](README-legacy.md#the-data-model-configuration).

## Operations and item selection

Operations may select root ETL items using `etlItemConfigCodeWhiteList` and
`etlItemConfigCodeBlackList`. Manually assigned `configCode` values must be
unique. Child operations inherit omitted lists from their parent operation,
and all references are validated before execution.

See [The Operation configuration](README-legacy.md#the-operation-configuration).

## Connector architecture notes

These documents record the architecture at each implementation phase. Earlier
phases may show intermediate configuration forms; the contract documented in
this README is authoritative for the current version.

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
