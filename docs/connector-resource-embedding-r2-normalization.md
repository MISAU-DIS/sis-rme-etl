# Connector resource embedding — R2 normalization

## Outcome

Resource resolution now accepts the configuration embedded in `SrcConf` and
each `DstConf`, while preserving both transitional top-level resources and the
original JDBC fallback.

The effective source resource is selected from:

1. `srcConf.resource`;
2. top-level `srcResource`;
3. the legacy JDBC source when neither is present.

For each destination, the effective resource is selected from:

1. `dstConf.resource`;
2. the corresponding top-level `dstResources` entry;
3. the legacy JDBC destination when neither is present.

## Duplicate declarations

An embedded and a top-level declaration may coexist during migration only when
their `connector`, `name` and `properties` are equal. Equivalent declarations
are normalized to the embedded instance. Conflicting declarations fail during
validation or initialization with an error identifying source or destination
index.

## Runtime boundary

The normalized configuration is passed through the existing connector
resolver. The resolved objects are still stored on `EtlItemConfiguration` in
this phase; `SrcConf.resolvedResource` and `DstConf.resolvedResource` remain
unset. Moving runtime ownership to those configurations belongs to R3.
