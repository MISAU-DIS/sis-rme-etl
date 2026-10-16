# Connector architecture — phase 1 contracts

## Outcome

Phase 1 introduces connector-neutral vocabulary alongside the current JDBC
implementation. It does not redirect execution and does not change existing ETL
JSON configuration.

The new model distinguishes two concepts:

- `ConnectorEndpoint`: the external system and its authentication/location;
- `ConnectorResource`: the named element used by an ETL item within that
  endpoint.

For JDBC, the endpoint is a database connection and the resource is a table.
For future connectors, the same resource name can identify a REST route, queue,
topic or file without adding those concepts to `EtlItemConfiguration`.

## Contracts

| Contract | Responsibility |
|---|---|
| `ConnectorType` | stable technology identifier (`JDBC`, `REST`, `MESSAGE_QUEUE`, `FILE`, `CUSTOM`) |
| `ConnectorDirection` | source or destination role in an ETL item |
| `ConnectorCapability` | behaviour advertised explicitly by the connector |
| `ConnectorEndpoint` | neutral URI, credentials, capabilities and native unwrap boundary |
| `ConnectorResource` | neutral resource name, direction and endpoint association |

Capability validation is intentionally independent from connector type. An
operation will eventually request behaviours such as `READ`, `WRITE`,
`TRANSACTIONS`, `PAGINATION`, or `ACKNOWLEDGEMENT`; configuration validation can
then reject an incompatible combination before processing starts.

## JDBC compatibility adapters

`JdbcConnectorEndpoint` wraps `DBConnectionInfo` and maps:

| Legacy property | Neutral property |
|---|---|
| `connectionURI` | `uri` |
| `dataBaseUserName` | `userName` |
| `dataBaseUserPassword` | `password` |

`JdbcTableResource` wraps `TableConfiguration` and maps `tableName` to `name`.
Both adapters expose `unwrap(...)` as a controlled bridge for migration code.
The endpoint `toString()` deliberately excludes the password.

## Compatibility boundary

During this phase:

- `srcConnInfo` and `dstConnInfo` remain the authoritative connection objects;
- `srcConf.tableName` and `dstConf.tableName` remain authoritative;
- `Engine`, `TaskProcessor`, transaction strategies and DAOs continue using
  JDBC directly;
- no REST, messaging or file connector is selectable yet;
- no legacy property is deprecated or renamed.

The next phase can introduce configuration DTOs/factories that create these
contracts while treating missing connector type as `JDBC`.

## Acceptance criteria

1. Existing JDBC configuration and baseline tests remain green.
2. A `DBConnectionInfo` can be viewed through neutral endpoint terminology.
3. A `TableConfiguration` can be viewed as a neutral named resource.
4. JDBC capabilities are explicit and immutable.
5. Missing capabilities fail validation with a specific exception.
6. Credentials are not exposed by adapter diagnostics.
