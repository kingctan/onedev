package com.pmease.commons.editable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.pmease.commons.loader.AppLoader;
import com.pmease.commons.loader.ImplementationRegistry;

@SuppressWarnings("serial")
public abstract class AbstractPolymorphicPropertyEditContext extends PropertyEditContext {

	private final List<Class<?>> implementations = new ArrayList<Class<?>>();
	
	private BeanEditContext valueContext;

	public AbstractPolymorphicPropertyEditContext(Serializable bean, String propertyName) {
		super(bean, propertyName);

		Class<?> elementBaseClass = getPropertyGetter().getReturnType();
		ImplementationRegistry registry = AppLoader.getInstance(ImplementationRegistry.class);
		implementations.addAll(registry.getImplementations(elementBaseClass));
		
		Preconditions.checkArgument(
				!implementations.isEmpty(), 
				"Can not find implementations for '" + elementBaseClass + "'.");
		
		EditableUtils.sortAnnotatedElements(implementations);
		
		propertyValueChanged(getPropertyValue());
	}

	public List<Class<?>> getImplementations() {
		return implementations;
	}
	
	protected void propertyValueChanged(Serializable propertyValue) {
		if (propertyValue != null) {
			EditSupportRegistry registry = AppLoader.getInstance(EditSupportRegistry.class);
			valueContext = registry.getBeanEditContext(propertyValue);
		} else {
			valueContext = null;
		}
	}


	@Override
	public void setPropertyValue(Serializable propertyValue) {
		super.setPropertyValue(propertyValue);
		propertyValueChanged(propertyValue);
	}

	public BeanEditContext getValueContext() {
		return valueContext;
	}

	@Override
	public Map<Serializable, EditContext> getChildContexts() {
		if (valueContext != null)
			return valueContext.getChildContexts();
		else
			return new HashMap<>();
	}

}
