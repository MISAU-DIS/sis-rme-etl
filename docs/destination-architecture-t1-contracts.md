# Destination architecture — T1 neutral contracts

## Scope

T1 introduces the connector-neutral destination vocabulary without changing the
runtime ownership of `dstConf`. Existing ETL configuration and execution still
use `List<DstConf>` until T2 performs normalization.

## New aggregate

`EtlDestinationConfiguration` represents one logical destination. It owns:

- `connector`;
- `resource`;
- logical destination `name`;
- destination record `fields`;
- optional logical `primaryKey`;
- zero or one physical destination variant;
- manual, generated and resolved mappings;
- join mappings, excluded and ignorable fields;
- transformer selection and destination apply condition;
- mapping-resolution and missing-value policies;
- multiple-data-source conflict policies;
- identified-record update policy;
- destination-as-data-source behaviour;
- the resolved variant and, in later phases, its resolved runtime resource;
- its parent ETL item.

The aggregate is a complete logical transformation target by itself. No
explicit destination type or `primary` discriminator is required. When an
adapter is present, its type is inferred from the configured adapter property.
When it is omitted, the resource and its resolved connector can create the
adapter automatically.

## Destination variants

The supported structural variants are:

- `tableDataDestination` — `JdbcTableDestinationConfiguration`;
- `jsonDataDestination` — `JsonDestinationConfiguration`;
- `csvDataDestination` — `CsvDestinationConfiguration`.

`JdbcTableDestinationConfiguration` extends the current `DstConf`. This is an
intentional compatibility bridge: all existing JDBC behaviour stays in place
while neutral callers gain a destination-variant contract.

JSON and CSV add only format-specific capabilities; CSV, for example, owns its
record-format options. The logical name, fields and primary key shared by
transformation and persistence are owned by `EtlDestinationConfiguration`,
rather than repeated inside each physical variant. Runtime writer integration
belongs to later phases.

## Common contract

`DestinationDataConfiguration` exposes optional adapter capabilities:

- discovered primary and unique keys;
- parent, child and shared-primary-key relationships;
- parent destination aggregate;
- supported `ConnectorWriteMode` values.

The JDBC variant currently advertises all existing write modes. JSON and CSV
advertise `INSERT` only. Runtime capability validation will use this contract
after destinations are integrated into `EtlItemConfiguration`.

A root `primaryKey` is valid with or without an adapter. For JDBC it takes
precedence as explicit configuration. When it is absent, the JDBC adapter may
discover the physical primary key during `fullLoad`, which is then promoted to
the aggregate.

Adapter resolution uses the following precedence:

1. an explicitly configured adapter;
2. `resource.properties.format` (`json` or `csv`);
3. the resource filename extension (`.json` or `.csv`);
4. the resolved connector type.

A resolved JDBC connector creates `JdbcTableDestinationConfiguration`
automatically, using the resource name as the physical table name. The
resolved JDBC resource and connection information are propagated to that
adapter. Explicit adapters are checked for compatibility with the resolved
connector, so a JSON adapter cannot silently run over a JDBC connector.

## Legacy-rule bridge

`copyTransformationRulesFrom(DstConf)` copies only logical transformation and
conflict-resolution rules into the aggregate. It deliberately does not copy
table name, connection information, JDBC metadata, keys or relationships. This
is the normalization seam that T2 can use without making `DstConf` the runtime
owner of transformation behaviour.

The aggregate also exposes the public destination contracts used by the
current runtime: `EtlTransformTarget`, `EtlDataSource`, `EtlDstConf` and
`EtlItemConfigurationComponent`. Consequently, lifecycle and selection
operations such as `fullLoad`, `isDisabled`,
`isDoNotUseSrcConfAsDataSource` and `shouldBeProcessed` already have a neutral
entry point. Logical state is owned by the aggregate; physical operations are
delegated to the selected destination variant. A physical operation not yet
implemented by a non-JDBC variant fails explicitly instead of silently being
treated as JDBC.

The aggregate owns the connector-neutral part of `fullLoad`. It collects the
primary and additional data sources, resolves preferred and non-preferred data
sources, loads the configured record transformer, initializes configured join
mappings and builds `allMapping` from manual/automatic mappings. Physical
metadata discovery remains delegated to the selected destination variant, when
one exists, and runs before this logical preparation.

## Structural validation

Initialization rejects:

- more than one configured destination variant;
- a resource nested under `tableDataDestination`;
- conflicting `destination.connector` and `destination.resource.connector`;
- missing resource for a non-JDBC destination;
- missing logical destination name;
- missing resource name when a resource is present;
- missing, unnamed or duplicated root `destination.fields` for adapter-free,
  JSON or CSV destinations.

The resource belongs to the destination aggregate, matching the established
source ownership rule.

## Runtime boundary

T1 deliberately does not modify:

- `EtlItemConfiguration.dstConf`;
- `ConnectorRecordWriters` indexing;
- `EtlLoadHelper` destination contracts;
- transformation target ownership;
- legacy JSON configuration.

Those changes start in T2 and T3. This keeps the new vocabulary independently
testable before it participates in production execution.

## Verification

`EtlDestinationConfigurationTest` verifies inference, parent association,
public legacy-contract compatibility, lifecycle capabilities, resource
ownership, adapter-free loading with a root primary key, record schema
requirements, ambiguity rejection and strict unknown-property handling.

