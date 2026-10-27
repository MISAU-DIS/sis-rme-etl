# Connector resource embedding — R4 consumers

## Outcome

Runtime consumers now obtain connector resources from the configuration that
owns them:

- the source reader uses `SrcConf.resolvedResource`;
- destination writer creation iterates `DstConf` and uses each
  `DstConf.resolvedResource`;
- destination type selection reads the resource directly from `DstConf`.

This removes the positional runtime association between `dstConf` and the old
`EtlItemConfiguration.resolvedDstResources` list. Reordering or selecting a
destination no longer requires a parallel resolved-resource collection to stay
aligned.

## Compatibility API

`EtlItemConfiguration.getResolvedSrcResource()`,
`getResolvedDstResource(DstConf)` and `getResolvedDstResources()` remain
available for existing callers. They are now views over the resources owned by
`SrcConf` and `DstConf`; `EtlItemConfiguration` no longer stores duplicate
resolved-resource state.

Top-level `srcResource` and `dstResources` declarations remain supported as
transitional input and are normalized before runtime ownership is assigned.
Because resources no longer have item-level runtime storage, every top-level
source or destination declaration must have a corresponding `srcConf` or
`dstConf` owner. Invalid ownerless declarations fail during validation and
initialization.
