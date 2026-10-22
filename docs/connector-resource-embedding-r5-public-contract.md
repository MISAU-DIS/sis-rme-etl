# Connector resource embedding — R5 public contract

## Outcome

The public documentation now presents `srcConf.resource` and
`dstConf[].resource` as the canonical connector-resource configuration. This
keeps extraction context with `srcConf` and transformation/loading context with
each `DstConf`.

The former top-level `EtlItemConfiguration.srcResource` and `dstResources`
properties and their Java accessors were removed completely. Resource
resolution no longer contains precedence, conflict detection or positional
normalization between two representations.

Legacy JDBC configurations remain supported: when an embedded `resource` is
absent, the resource is derived from the corresponding `srcConf` or `dstConf`
and the internal legacy JDBC connector.

The README architecture diagram, primary resource example and FILE destination
example now use the embedded form.
