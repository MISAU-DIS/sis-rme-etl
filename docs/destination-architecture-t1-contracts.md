# Destination architecture — T1 neutral contracts

## Scope

T1 introduces the connector-neutral destination vocabulary without changing the
runtime ownership of `dstConf`. Existing ETL configuration and execution still
use `List<DstConf>` until T2 performs normalization.

## New aggregate

`EtlDestinationConfiguration` represents one logical destination. It owns:

- `connector`;
- `resource`;
- destination record `fields`;
- exactly one physical destination variant;
- manual, generated and resolved mappings;
- join mappings, excluded and ignorable fields;
- transformer selection and destination apply condition;
- mapping-resolution and missing-value policies;
- multiple-data-source conflict policies;
- identified-record update policy;
- destination-as-data-source behaviour;
- the resolved variant and, in later phases, its resolved runtime resource;
- its parent ETL item.

The aggregate follows the source inference model. No explicit destination type
or `primary` discriminator is required.

## Destination variants

The supported structural variants are:

- `tableDataDestination` — `JdbcTableDestinationConfiguration`;
- `jsonDataDestination` — `JsonDestinationConfiguration`;
- `csvDataDestination` — `CsvDestinationConfiguration`.

`JdbcTableDestinationConfiguration` extends the current `DstConf`. This is an
intentional compatibility bridge: all existing JDBC behaviour stays in place
while neutral callers gain a destination-variant contract.

JSON and CSV variants own their logical name. CSV additionally owns its
record-format options. The fields shared by transformation and persistence are
owned by `EtlDestinationConfiguration`, rather than repeated inside each
physical variant. Their physical key and relationship descriptions are
neutral and do not require JDBC metadata. They are configuration contracts only
in T1; runtime writer integration belongs to later phases.

## Common contract

`DestinationDataConfiguration` exposes:

- logical destination name;
- primary and unique keys;
- parent, child and shared-primary-key relationships;
- parent destination aggregate;
- supported `ConnectorWriteMode` values.

The JDBC variant currently advertises all existing write modes. JSON and CSV
advertise `INSERT` only. Runtime capability validation will use this contract
after destinations are integrated into `EtlItemConfiguration`.

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

## Structural validation

Initialization rejects:

- no configured destination variant;
- more than one configured destination variant;
- a resource nested under `tableDataDestination`;
- conflicting `destination.connector` and `destination.resource.connector`;
- missing resource for a non-JDBC destination;
- missing logical destination name;
- missing resource name when a resource is present;
- missing, unnamed or duplicated root `destination.fields` for JSON/CSV.

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
ownership, record schema requirements, ambiguity rejection and strict
unknown-property handling.

