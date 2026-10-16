# Connector resource embedding — R3 runtime ownership

## Outcome

`EtlItemConfiguration.initializeConnectorResources()` still performs connector
selection and resource resolution exactly once, after normalization. Once all
resources resolve successfully, it assigns the source result to
`SrcConf.resolvedResource` and each destination result to the corresponding
`DstConf.resolvedResource`.

Resolution is prepared before assignment so a configuration error cannot leave
only part of an ETL item's configurations initialized.

## Compatibility mirrors

The transient `EtlItemConfiguration.resolvedSrcResource` and
`resolvedDstResources` properties remain populated for existing consumers.
Compatibility getters prefer the resource owned by `SrcConf` or `DstConf` and
fall back to the item-level mirror only when necessary.

Reader and writer factories still iterate the item-level values in R3. Moving
those consumers to the embedded ownership and removing positional runtime
association belongs to R4.
