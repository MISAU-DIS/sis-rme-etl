package org.openmrs.module.epts.etl.etl.processor.transformer;

public enum FunctionTransformerType {
	TO_STRING {
		@Override
		public Object apply(Object input) {
			return input != null ? input.toString() : null;
		}

		@Override
		public boolean requiresInput() {
			return true;
		}
	},

	EMPTY_STRING {
		@Override
		public Object apply(Object input) {
			return "";
		}
	},

	SPACE {
		@Override
		public Object apply(Object input) {
			return " ";
		}
	},

	NEW_LINE {
		@Override
		public Object apply(Object input) {
			return System.lineSeparator();
		}
	},

	TAB {
		@Override
		public Object apply(Object input) {
			return "\t";
		}
	};

	public abstract Object apply(Object input);

	public boolean requiresInput() {
		return false;
	}

	public boolean acceptsInput() {
		return requiresInput();
	}
}