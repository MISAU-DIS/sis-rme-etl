# Acumulação de resultados ETL

## Contrato

- Uma chave pertence a uma única categoria. A última actualização substitui a anterior e move o resultado para o fim da categoria.
- A identidade é a chave primária completa, incluindo os nomes dos componentes, dentro da **mesma instância de configuração**. Configurações diferentes não são deduplicadas, mesmo quando apontam para uma tabela com o mesmo nome. Isto evita misturar origens, destinos e configurações de consultas. Sem configuração, o âmbito é a classe concreta do registo.
- Componentes numéricos equivalentes são normalizados. A ordem dos componentes não importa. Chaves incompletas ou com tipos não suportados usam identidade da instância; UUID e chaves únicas alternativas não são usados como substitutos da chave primária.
- A primeira chave observada de cada instância é mantida durante a vida do cabeçalho. Alterar posteriormente o ID não muda a identidade desse registo. Uma instância nova deve apresentar a chave original para representar a mesma operação. O merge transporta a chave original do cabeçalho de origem.
- Os getters de categorias devolvem listas não modificáveis, vazias em vez de null, com cópias dos itens. Os setters e métodos de inserção copiam a classificação. Alterar um item exige uma nova chamada a addOrUpdate para actualizar o cabeçalho.
- Mantém-se a API mutável de EtlOperationItemResult para os produtores existentes. As cópias isolam o tipo, a excepção atribuída e a estrutura da lista de inconsistências; os registos, excepções e objectos InconsistenceInfo continuam partilhados. Não se trata de uma cópia profunda do domínio.
- O acumulador pertence a uma tarefa e não suporta escritores concorrentes. Os índices de identidade retêm as instâncias observadas até o cabeçalho ser libertado, incluindo instâncias substituídas. Não deve ser usado como acumulador global ilimitado.

## Agregação e progresso

addAllFromOtherResult adopta as classificações recebidas, preserva a primeira excepção fatal e mantém o intervalo e os registos de entrada do receptor. Repetir uma agregação não duplica resultados; autoagregação não faz nada.

countAllSuccessfulyProcessedRecords mantém o contrato histórico usado pelo Engine: quantidade de entradas processadas menos resultados inesperados e inconsistências não resolvidas. getAllSuccessfulyProcessedRecords devolve as saídas explicitamente classificadas como NO_ERROR ou RESOLVED_INCONSISTENCES. Estes universos podem diferir; não se alterou silenciosamente a métrica do motor.

TaskProcessor acrescenta agora entradas sem copiar todo o histórico. Os produtores usam addSuccessfulRecords para evitar listas intermédias de wrappers. A API anterior addAllToRecordsWithNoError continua disponível.

## Persistência e igualdade do domínio

EtlResultErrorDocumenter concentra a persistência de erros. O adaptador documentErrors passa-lhe os erros reais, corrigindo o ciclo anteriormente vazio. O teste usa uma inconsistência com persistência simulada; não foi executado contra uma base de dados real. Resultados sem excepção nem inconsistência não têm diagnóstico para persistir.

Não se alterou a igualdade global de AbstractDatabaseObject, Oid ou EtlOperationItemResult: o novo índice não depende desses métodos. Rever os contratos globais de equals/hashCode exige uma alteração separada devido aos restantes consumidores do domínio.

## Validação em 15/09/2026

Compilação da API e 15 testes seleccionados passaram: EtlOperationResultHeaderTest, EngineProcessingStrategiesTest e EnginePersistenceCoordinatorTest. Cobrem transições, instâncias duplicadas, chaves compostas, isolamento de configurações, IDs mutáveis, snapshots, setters, ordem, autoagregação, excepção fatal, progresso e documentação de inconsistências.

O teste de 100 mil itens chama o método original addAllToRecordsWithNoError e verifica que não ocorre nenhuma comparação equals de registos. A verificação não depende de um limite frágil de tempo.

Medição sintética opcional com proxies, JVM 17, sem acesso à base de dados:

| Itens | Inserção | Actualização dos mesmos itens | Bytes alocados na inserção |
|---:|---:|---:|---:|
| 10 mil | 46,3 ms | 9,3 ms | 6,1 MiB |
| 100 mil | 260,9 ms | 51,3 ms | 58,8 MiB |
| 1 milhão | 1.224,7 ms | 568,9 ms | 520,2 MiB |

São medições de uma execução, sujeitas a JIT e GC, não previsões de produção. A criação dos registos de entrada e dos snapshots de saída fica fora da medição. Os bytes são alocações acumuladas da thread, não memória retida nem pico de heap. Não foi medido o tempo da implementação antiga; o custo quadrático anterior foi demonstrado pela pesquisa linear por inserção.

Reproduzir em PowerShell:

```powershell
mvn -pl api -am test '-Dtest=EtlOperationResultHeaderTest,EngineProcessingStrategiesTest,EnginePersistenceCoordinatorTest' '-Detl.result.benchmark=true' '-Dsurefire.failIfNoSpecifiedTests=false' '-DfailIfNoTests=false'
```

Sem etl.result.benchmark, a medição até um milhão de itens é ignorada; o teste funcional de 100 mil continua activo.
