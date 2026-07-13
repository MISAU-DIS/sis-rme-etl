package org.openmrs.module.epts.etl.conf.interfaces;

import java.util.Set;

import org.openmrs.module.epts.etl.conf.AtomicCondition;

public interface ConditionExpressionAdapter {

	/**
	 * Verifica se este adapter suporta a expressão informada.
	 */
	boolean supports(String expression);

	/**
	 * Converte a expressão numa condição atómica normalizada.
	 *
	 * @param expression expressão completa
	 * @param operator   operador externo, ou null quando a expressão aparece
	 *                   sozinha
	 * @param values     valores externos, ou coleção vazia quando a expressão
	 *                   aparece sozinha
	 */
	AtomicCondition toAtomicCondition(String expression, String operator, Set<String> values);
}