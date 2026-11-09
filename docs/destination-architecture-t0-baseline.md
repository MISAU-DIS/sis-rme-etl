# Destination architecture — T0 baseline

## Purpose

This phase freezes the observable destination behaviour before introducing the
connector-neutral destination aggregate. It makes no runtime or configuration
change. The baseline identifies the responsibilities currently concentrated in
`DstConf`, the migration boundaries and the tests that must remain green during
T1–T8.

## Current configuration contract

An `EtlItemConfiguration` owns `List<DstConf> dstConf`. Every `DstConf` is both a
logical transformation target and a physical table configuration, even when its
resolved resource is a FILE connector.

Each destination currently owns:

- its connector resource declaration and resolved resource;
- destination record schema;
- transformation mappings and mapping policies;
- JDBC table metadata, keys and relationships;
- destination record identification behaviour;
- destination type and persistence-specific options;
- the available and preferred source data sets used during transformation.

`EtlItemConfiguration` resolves one resource per `DstConf` and
`ConnectorRecordWriters` indexes the resulting writer using the `DstConf`
instance identity.

## Current runtime flow

```text
EtlItemConfiguration.dstConf[]
        |
        +-- DstConf.resource / legacy destination connection
        |       |
        |       +-- ConnectorResource
        |               |
        |               +-- ConnectorRecordWriter
        |
        +-- EtlTransformTarget
        |       |
        |       +-- mapping and record transformation
        |
        +-- AbstractTableConfiguration
                |
                +-- JDBC metadata, keys and SQL behaviour
```

The writer boundary is already connector-neutral. The configuration and
transformation boundaries are not: they still identify destinations by
`DstConf` and therefore expose table semantics to every destination.

## Dependency inventory

At this baseline, 40 production files reference `DstConf` directly:

| Area | Files | Main dependency |
|---|---:|---|
| Configuration | 12 | ownership, initialization, cloning and validation |
| Transformation | 8 | mappings, target schema, parents and defaults |
| Runtime/processors | 7 | routing, loading and destination selection |
| Model | 6 | ETL state and destination record relationships |
| Connector layer | 3 | resource-to-writer indexing and JDBC adapter |
| Other operations | 4 | validation and specialized processing |

The first migration seam is not persistence. Persistence already operates
through `ConnectorRecordWriter`. The first seam is destination identity:

- `List<DstConf>` in `EtlItemConfiguration`;
- `Map<DstConf, ConnectorResource>` in `ConnectorRecordWriters`;
- methods accepting `DstConf` in `EtlLoadHelper`, processors and transformers.

## Responsibilities to separate

### Logical destination responsibilities

These belong in the future `EtlDestinationConfiguration` aggregate:

- mappings and mapping resolution policies;
- transformation conditions and transformer selection;
- missing, null and incomplete mapping behaviour;
- preferred and available data sources;
- record identification action;
- association with the connector and resource;
- resolution of exactly one physical destination variant.

### Physical destination responsibilities

These belong in a `DestinationDataConfiguration` implementation:

- destination name and record schema;
- supported write modes;
- format-specific options;
- physical metadata needed by its writer.

JDBC-only responsibilities must remain in
`JdbcTableDestinationConfiguration`:

- table, schema, primary and unique keys;
- parent, child and shared-primary-key relationships;
- SQL generation and database metadata;
- JDBC connection and DAO integration;
- database-model and generated-POJO integration.

## Behavioural invariants

The following behaviour must remain unchanged throughout the migration:

1. One ETL item may have multiple destinations.
2. Every destination may select a different connector and resource.
3. Destinations using the same endpoint may share the appropriate runtime
   connection without sharing logical configuration.
4. Legacy `dstConf` configuration remains valid until the explicit cleanup
   phase.
5. A destination resource is resolved before processing starts.
6. Connector direction and WRITE capability are validated before creating a
   writer.
7. Transformation mappings remain destination-specific.
8. Parent/child and on-demand-parent processing preserve their current target.
9. JDBC insert, update, delete and identity behaviour remain unchanged.
10. Destination commits happen before source checkpoint persistence.
11. A commit failure leaves the interval pending.
12. FILE destinations continue to receive the transformed destination record,
    not the source record.

## Existing executable baseline

The current suite already protects the principal seams:

- `EtlConnectorConfigurationTest`
  - legacy destination resolution;
  - embedded destination resources;
  - mixed JDBC and FILE destinations;
  - different connectors in the same ETL item;
  - independent JDBC connection information;
  - lazy destination connection aggregation;
  - destination record-schema loading.
- `JdbcConnectorRecordWriterTest`
  - JDBC writer routing and result handling.
- `FileConnectorRecordWriterTest`
  - non-JDBC output persistence.
- `CheckpointCommitOrderingTest`
  - destination commit and source checkpoint ordering.
- `DstConfIdGeneratorManagerTest`
  - destination identity generation isolation.
- `JdbcBaselineLegacyTestsBridgeTest`
  - legacy JDBC ETL behaviour.

The baseline command is:

```text
mvn -pl api test
```

## Migration boundaries

T1 may add neutral interfaces and configuration classes, but must not replace
`dstConf` in the runtime.

T2 may normalize legacy `dstConf` into the new aggregate, but downstream code
must still observe equivalent destination order and identity.

T3 changes writer indexing from `DstConf` to the neutral destination identity.
It must preserve per-resource writer lookup and connection sharing.

T4 moves transformation responsibilities to the aggregate. JDBC table metadata
must not leak back into the neutral contracts.

Only after those boundaries are stable should JSON and CSV physical destination
variants be introduced.

## T0 exit criteria

- No production behaviour is changed.
- The destination dependency inventory is recorded.
- The behavioural invariants and migration seams are explicit.
- The complete API test suite passes unchanged.

