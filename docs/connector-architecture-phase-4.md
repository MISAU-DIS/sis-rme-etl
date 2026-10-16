# Connector architecture — phase 4 destination execution port

## Current increment

Phase 4 introduces a destination port symmetric with the source reader:

- one `ConnectorRecordWriter` is associated with exactly one resolved
  `ConnectorResource`;
- `ConnectorWriteRequest` carries an immutable batch and its persistence mode;
- `ConnectorRecordWriterFactory` validates destination direction and `WRITE`
  capability, then selects the adapter from the resource connector type;
- `JdbcConnectorRecordWriter` owns only its resource and JDBC connection and
  translates the request into the corresponding DAO operation;
- `EtlItemConfiguration.createDestinationWriters()` creates the aggregate and
  associates each `DstConf` with its resolved resource.

The execution does not transport `srcConn` and `dstConn` through the engine
contracts anymore. It transports a `ConnectorRecordReader` for the source and a
`ConnectorRecordWriters` aggregate for the destinations. The aggregate:

- indexes every writer by the exact resolved `ConnectorResource` instance;
- also maps each legacy `DstConf` to its resolved resource during transition;
- reuses one native session for resources on the same endpoint;
- allows the loading code to request only the writer for the destination being
  persisted.

The writer does not know `TaskProcessor`, the source reader, the engine or the
environment in which it was invoked. Its responsibility is limited to accepting
a write request and returning the persistence result.

## ETL orchestration boundary

`EtlLoadHelper` keeps the ETL-specific preparation: parent handling, conflict
resolution, shared-primary-key checks, classification of records as inserts or
updates and post-load processing. Once a final database operation is known, it
creates the writer for that destination resource and submits a declarative
request (`INSERT`, `UPDATE`, `UPDATE_ALL` or `DELETE_ALL`).

This boundary allows future REST, messaging and file writers to implement the
same contract without depending on JDBC execution context or ETL processors.

## Remaining phase-4 migration

JDBC connections now originate inside `EtlItemConfiguration` while it builds
the reader and writer aggregate. Some transformation, tracking and transaction
lifecycle implementations still unwrap the native JDBC session internally;
those are compatibility details below the connector-facing engine contracts.

`TaskProcessor` is connector-neutral: it contains no `Connection`, `srcConn`,
`dstConn` or native `unwrap` usage. JDBC-only processors temporarily inherit
`JdbcTaskProcessor`, the explicit compatibility boundary to remove while each
specialized processor is migrated.
