# Connector architecture — phase 0 baseline

## Purpose

This phase freezes the observable JDBC behaviour before source and destination
connectors are introduced. It deliberately does not change runtime architecture,
configuration names, SQL generation, or persistence.

The baseline has three roles:

1. identify the JDBC assumptions that must later move behind connector ports;
2. preserve compatibility for existing ETL JSON files;
3. define the regression gates for each migration phase.

## Current execution path

```text
EtlConfiguration
  ├─ srcConnInfo: DBConnectionInfo
  ├─ dstConnInfo: DBConnectionInfo
  └─ etlItemConfiguration
       ├─ srcConf: SrcConf/TableConfiguration
       └─ dstConf: DstConf/TableConfiguration
              │
OperationController → Engine/TaskProcessor
              │
              ├─ extract: AbstractEtlSearchParams + DatabaseObjectDAO + JDBC Connection
              ├─ transform: EtlRecordTransformer/Field transformers
              ├─ load: EtlLoadHelper + DatabaseObjectDAO + JDBC Connection
              └─ auxiliary state: stage area, parents, conflicts and progress in JDBC
```

Transformation is already the most reusable part. Extraction, loading,
transaction control, metadata discovery, and auxiliary persistence currently
receive `java.sql.Connection` directly or call JDBC-oriented helpers.

## Coupling inventory

The inventory was generated from `api/src/main/java` by locating Java files that
refer to `java.sql.Connection`, `OpenConnection`, `DatabaseObjectDAO`, or
`DBUtilities`. Counts are a navigation aid, not a quality metric.

| Area | Files with a direct JDBC dependency | Main responsibility |
|---|---:|---|
| `conf` | 54 | connection and table configuration, SQL/metadata preparation, parents and mappings |
| `etl` | 35 | extraction, transformation orchestration, loading, stage area and default parents |
| `model` | 20 | record persistence, searches, progress and generic database objects |
| `utilities` | 15 | connection lifecycle, DAO, SQL and metadata utilities |
| `engine` | 7 | connection acquisition, transaction boundaries and checkpoints |
| operation-specific packages | 56 | detectors, merge, load/export, reconciliation and preparation |
| controllers/common/reporting | 10 | operation startup, finalization and auxiliary persistence |

Cross-cutting totals at the time of this baseline:

- 193 files import or reference `java.sql.Connection`;
- 46 files reference `OpenConnection`;
- 27 files call or reference `DatabaseObjectDAO`;
- 26 files call or reference `DBUtilities`.

The first extraction boundaries to isolate in later phases are
`Engine.extract(...)`, `AbstractEtlSearchParams`, and
`EtlDatabaseObjectSearchParams`. The first destination boundaries are
`EtlLoadHelper`, `DatabaseObjectDAO`, and the persistence calls exposed by
`TableConfiguration`. Transaction ownership currently belongs to `Engine` and
its processing strategies.

## Current capability matrix

| Capability | Current implementation | Phase-0 classification |
|---|---|---|
| Primary source | JDBC table/query | JDBC-only |
| Primary destination | JDBC table | JDBC-only |
| JSON/CSV/dump output | `EtlDstType` special destination behaviour | existing specialised output; not yet a connector |
| Console/popup output | `EtlDstType` immediate output | existing specialised output; not yet a connector |
| REST input/output | custom Java processor required | unsupported declaratively |
| Message queue input/output | custom Java processor required | unsupported declaratively |
| Schema discovery | `TableConfiguration.fullLoad`/static physical metadata | relational/JDBC model |
| Parents/shared PK/unique keys | `TableConfiguration` and `EtlDatabaseObject` | relational semantics |
| Conflict resolution | `EtlLoadHelper`/DAO/table keys | JDBC-only |
| Stage area | database tables and DAO | JDBC-only |
| Progress/checkpoints | database-backed progress plus engine interval files/state | coupled to current engine lifecycle |
| Transactions | `OpenConnection` and JDBC commit/rollback | JDBC-only |

## Operation boundary

Until an operation is explicitly migrated and receives a connector capability
declaration, every existing `EtlOperationType` keeps its current behaviour.

The following are explicitly JDBC-only in the first transition increment:

- `ETL`, `DB_EXTRACT`, `LOAD`, `EXPORT`, `QUICK_EXPORT`, and `QUICK_LOAD`;
- database merge and synchronization operations;
- changed/new/missing/outdated/phantom record detectors and gap detection;
- conflict and inconsistency resolution;
- consolidation, reconciliation, database preparation, and data-model generation.

`GENERIC_OPERATION` and processor-specific behaviour are not automatically
portable. Their supported connector types must eventually be declared by the
processor instead of inferred.

## Compatibility invariants

The transition must preserve all of the following for JDBC configurations:

- existing JSON properties such as `srcConnInfo`, `dstConnInfo`,
  `dataBaseUserName`, `dataBaseUserPassword`, `connectionURI`,
  `driveClassName`, and `tableName` continue to deserialize;
- omission of a future connector type continues to select JDBC;
- SQL extraction order, field mappings, parent resolution, shared primary keys,
  default values, and conflict handling remain unchanged;
- successful work is checkpointed only after all required commits;
- a commit or auxiliary-persistence failure leaves the interval pending;
- stage-area requests stay isolated by operation owner;
- current file and immediate destination classifications remain valid;
- dynamic and precompiled data-model modes retain their present behaviour.

## Executable characterization coverage

The principal baseline tests are:

| Behaviour | Characterization test |
|---|---|
| legacy JDBC connection properties and `tableName` | `JdbcConfigurationCompatibilityTest` |
| data-model configuration compatibility | `EtlConfigurationDataModelTest`, `EtlConfigurationClassPathTest` |
| transaction/commit/checkpoint ordering across strategies | `CheckpointCommitOrderingTest` |
| connection finalization and keep-alive lifecycle | `OpenConnectionFinalizationTest`, `ConnectionKeepAliveLifecycleTest` |
| processing strategy selection and completion | `EngineProcessingStrategiesTest`, `ParallelSearchCompletionTest`, `EngineStopLifecycleTest` |
| deferred persistence and owner isolation | `EnginePersistenceCoordinatorTest`, `StageAreaPersistenceRequestTest` |
| operation-result conflict/error categories | `EtlOperationResultHeaderTest` |
| static/dynamic physical metadata equivalence | tests under `conf/physical` |

A material part of this characterization suite still uses JUnit 4 while Maven
runs the JUnit Platform. `JdbcBaselineLegacyTestsBridgeTest` executes the
selected, passing JDBC lifecycle suite through `JUnitCore` without globally
enabling every legacy test.

Enabling the Vintage engine during this audit discovered three pre-existing
expectation mismatches which are intentionally not hidden or changed by this
phase:

- the two `PrecompiledSchemaFullLoadTest` scenarios do not capture the expected
  `Full load done using existing static data` message;
- `OpenConnectionFinalizationTest.alreadyClosedConnectionMustNotBeReportedAsCommitted`
  expects an exception which the current implementation does not throw.

These tests need a separate functional decision: either restore the documented
runtime behaviour or update their expectations after confirming that the new
behaviour is intentional.

Full database integration scenarios remain necessary before a migrated
connector is declared production-ready: extraction with filters and pagination,
parent resolution, each conflict strategy, stage-area replay, and rollback of a
partially processed interval.

## Exit criteria for phase 0

Phase 0 is complete when:

1. the compatibility and existing regression tests pass;
2. no production class or configuration format has changed;
3. the JDBC-only operation boundary above is accepted;
4. phase 1 can introduce connector-neutral contracts without deleting or
   renaming the legacy JDBC properties.

