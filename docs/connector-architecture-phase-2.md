# Connector architecture — phase 2 connector catalog

## Outcome

Phase 2 adds a reusable catalog of connector configurations and lets every ETL
item resource select its endpoint. Connectors are not intrinsically sources or
destinations: their role is determined by how a resource uses them.

The JDBC execution path remains authoritative and unchanged in this phase.

## Configuration

```json
{
  "connectors": [
    {
      "id": "openmrs-source",
      "type": "JDBC",
      "uri": "jdbc:mysql://source-host/openmrs",
      "userName": "reader",
      "password": "...",
      "properties": {
        "driverClassName": "com.mysql.cj.jdbc.Driver",
        "schema": "openmrs"
      }
    },
    {
      "id": "migration-database",
      "type": "JDBC",
      "uri": "jdbc:mysql://destination-host/migration",
      "userName": "writer",
      "password": "..."
    },
    {
      "id": "clinical-events",
      "type": "MESSAGE_QUEUE",
      "uri": "tcp://broker:61616",
      "properties": { "provider": "ACTIVEMQ" }
    }
  ],
  "defaultSourceConnector": "openmrs-source",
  "defaultDestinationConnector": "migration-database",
  "etlItemConfiguration": [
    {
      "srcResource": {
        "connector": "openmrs-source",
        "name": "patient"
      },
      "dstResources": [
        {
          "connector": "migration-database",
          "name": "patient"
        },
        {
          "connector": "clinical-events",
          "name": "patient.updated"
        }
      ]
    }
  ]
}
```

Connector `type` defaults to `JDBC` when omitted. `properties` carries
connector-specific options without polluting the common contract.

## Identity and selection

Every configured connector has a required, unique `id`. The IDs `legacy-src`
and `legacy-dst` are reserved for compatibility.

For each resource, selection follows this order:

1. the resource's explicit `connector`;
2. `defaultSourceConnector` or `defaultDestinationConnector` according to use;
3. the matching legacy connector when the resource still comes from
   `srcConf`/`dstConf`;
4. the only explicitly configured connector, if exactly one exists;
5. otherwise configuration is ambiguous and validation fails.

The selected connector must provide `READ` when used by `srcResource` and
`WRITE` when used by `dstResources`. The same connector can serve both roles if
it provides both capabilities.

## Legacy compatibility

The registry synthesizes internal connectors without modifying existing
objects:

| Existing configuration | Internal connector |
|---|---|
| `srcConnInfo` | `legacy-src` |
| `dstConnInfo` | `legacy-dst` |

When neutral resources are absent, `srcConf.tableName` and every
`dstConf.tableName` continue to be wrapped directly. Existing ETL JSON files
therefore retain their current behaviour.

While execution remains JDBC-based, a neutral JDBC resource paired with a
legacy table must have the same `name` and `tableName`. Divergence is rejected
instead of allowing the configuration view and runtime view to disagree.

## Validation

Normal `EtlConfiguration.validate()` now detects:

- missing, duplicate or reserved connector IDs;
- unknown connector references and unknown defaults;
- ambiguous selection when more than one connector is available;
- source connectors without `READ`;
- destination connectors without `WRITE`;
- transitional JDBC resource names that differ from `tableName`.

REST, messaging and file configurations can be parsed, but validation rejects
their use until a concrete connector advertises the required capabilities.

## Security and lifecycle

Passwords are excluded from all new `toString()` implementations. Connector
IDs also provide the future lifecycle key for sharing connection pools, HTTP
clients or messaging sessions instead of creating them independently per item.

The connector catalog is initialized once, privately, during
`EtlConfiguration.init()`. Each `EtlItemConfiguration` then resolves its source
and destination resources once during its own `init()`. Runtime consumers only
read the resulting state through `getResolvedSrcResource()` and
`getResolvedDstResources()`; the destination collection is immutable.

Configuration validation uses a temporary resolution preview. It does not
publish runtime state or make resource resolution available as a public,
repeatable operation.

## Compatibility boundary

Actual execution still requires the legacy JDBC structures in this phase:

- `srcConnInfo` and, where needed, `dstConnInfo`;
- `srcConf` and `dstConf`;
- the current JDBC `Engine`, DAO, transaction and checkpoint flow.

The catalog and resource references are now ready to become the authoritative
runtime path in a later phase without reintroducing a global source/destination
limitation.
