# Connector resource embedding — R0 baseline

## Purpose

This phase freezes the behaviour that must survive moving resource declarations
from `EtlItemConfiguration` into `SrcConf` and `DstConf`. It makes no runtime or
configuration-model change.

## Current ownership

`EtlItemConfiguration` currently owns four resource properties:

- serializable `srcResource`;
- serializable `dstResources`;
- transient `resolvedSrcResource`;
- transient `resolvedDstResources`.

`initializeConnectorResources()` resolves them once. Reader and writer
creation then use the transient values. Destination configuration and resolved
resource are associated by their position in the `dstConf` and `dstResources`
lists.

The main runtime consumers are:

- `EtlItemConfiguration.createSourceReader()`;
- `EtlItemConfiguration.createDestinationWriters()`;
- `EtlItemConfiguration.bindDestinationWriters()`;
- `TaskProcessor.determineDstType()`;
- `EtlItemConfiguration.canQuickTransform()`;
- `EtlLoadHelper`, indirectly through `ConnectorRecordWriters`.

## Compatibility baselines

The following behaviours are protected before the migration starts:

1. A configuration containing only `srcConnInfo`, `dstConnInfo`, `srcConf` and
   `dstConf` is adapted to JDBC resources.
2. Top-level `srcResource` and `dstResources` select explicit connectors.
3. Multiple destination resources remain associated with the corresponding
   `DstConf` instances.
4. One ETL item may use different connector types for its destinations.
5. Connector direction and capabilities are validated before execution.
6. FILE writes remain UTF-8 JSONL, append complete records and reject paths
   outside their configured root.

## Migration constraint

The next phases may change where resources are declared and stored, but must
not change connector selection, transformation rules, writer behaviour or the
legacy JDBC fallback. The positional top-level form remains supported as a
compatibility input even after runtime association moves into `SrcConf` and
`DstConf`.
