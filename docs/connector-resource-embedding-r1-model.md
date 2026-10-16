# Connector resource embedding — R1 model

## Outcome

`SrcConf` and `DstConf` can now own a serializable
`ConnectorResourceConfiguration` through their `resource` property. Each also
has a transient, JSON-ignored `resolvedResource` slot for the runtime object
that a later phase will assign during `EtlItemConfiguration.init()`.

This phase deliberately does not change resolution or execution. The existing
top-level `srcResource`, `dstResources`, `resolvedSrcResource` and
`resolvedDstResources` remain authoritative.

## New configuration shape

```json
{
  "srcConf": {
    "tableName": "patient",
    "resource": {
      "connector": "source-db",
      "name": "patient"
    }
  },
  "dstConf": [
    {
      "tableName": "patient_export",
      "resource": {
        "connector": "jsonl-output",
        "name": "patients.jsonl"
      }
    }
  ]
}
```

## Compatibility

Resource configuration is copied when dynamic `SrcConf` and `DstConf`
instances are cloned. Runtime resolved resources are intentionally not copied;
they will be resolved for the cloned ETL item during initialization.

Existing JSON configurations and the current reader/writer creation flow are
unchanged until R2 introduces normalization.
