# Connector architecture — phase 6 first non-JDBC destination

## Outcome

Phase 6 introduces `FILE` as the first executable non-JDBC destination. The
initial format is JSON Lines (JSONL): each transformed destination record is
serialized as one JSON object followed by a line break.

The existing JDBC destination remains unchanged. Destination routing now uses
the connector type: JDBC resources continue through the established database
loading flow, while non-JDBC resources are submitted to their associated
`ConnectorRecordWriter`.

## Configuration

```json
{
  "connectors": [
    {
      "id": "jsonl-output",
      "type": "FILE",
      "uri": "/var/lib/etl/output"
    }
  ],
  "etlItemConfiguration": [
    {
      "dstResources": [
        {
          "connector": "jsonl-output",
          "name": "patients.jsonl"
        }
      ]
    }
  ]
}
```

`uri` identifies the destination root directory. The resource `name` is a
relative file path below that root, so subdirectories such as
`daily/patients.jsonl` are supported. A `file:` URI can also be used.

## Write semantics

- files and missing parent directories are created automatically;
- records are encoded as UTF-8;
- each request appends complete JSONL records to the destination;
- the first implementation supports `INSERT` requests only;
- an empty request still creates the configured destination file;
- resource paths that escape the configured root are rejected.

The writer is associated with exactly one `ConnectorResource` and does not
know about `TaskProcessor`, the engine, or JDBC connections. This keeps the
same connector-neutral contract available for future REST and messaging
destinations.

## Compatibility boundary

This phase changes only destination persistence. Extraction and the current
transformation rules may continue to depend on JDBC. Legacy destinations whose
type is selected by `EtlDstType.FILE` also remain available; they are distinct
from the new connector-based `FILE` destination.

