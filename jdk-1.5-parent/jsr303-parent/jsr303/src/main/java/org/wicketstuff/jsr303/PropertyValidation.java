package org.wicketstuff.jsr303;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.AbstractPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

/**
 * Behavior to be added to either a FormComponent or Form. If used on a FormComponent, it has to be
 * bound to an AbstractPropertyModel. When used on a Form, this behavior will be added to all
 * appropriate FormComponents.
 */
@SuppressWarnings("unchecked")
public class PropertyValidation extends Behavior
{

	private static final long serialVersionUID = 1L;

	class JSR303ValidatorFormComponentVisitor implements IVisitor<Component, Void>
	{


		public void component(Component component, IVisit<Void> visit)
		{

			if (component instanceof FormComponent<?>)
			{
				final FormComponent<?> fc = (FormComponent<?>)component;
				final IModel<?> model = fc.getModel();
				if (model != null)
				{
					if (model instanceof AbstractPropertyModel<?>)
					{
						final AbstractPropertyModel<?> pm = (AbstractPropertyModel<?>)model;
						PropertyValidator validator = new PropertyValidator(pm, fc);
						fc.add(validator);
					}
				}
			}
		}
	}

	private boolean assigned = false;

	@Override
	public synchronized void beforeRender(Component context)
	{
		if (!assigned)
		{
			assigned = true;
			if (context instanceof Form<?>)
			{
				final Form<?> form = (Form<?>)context;
				form.visitChildren(new JSR303ValidatorFormComponentVisitor());
			}
			else
			{
				if ((context instanceof FormComponent<?>))
				{
					final FormComponent<?> fc = (FormComponent<?>)context;
					final IModel<?> m = fc.getModel();
					if (m instanceof AbstractPropertyModel<?>)
					{
						final AbstractPropertyModel<?> apm = (AbstractPropertyModel<?>)m;
						fc.add(new PropertyValidator(apm, fc));
					}
					else
					{
						throw new IllegalArgumentException(
							"Expected something that provides an AbstractPropertyModel");
					}
				}
				else
				{
					throw new IllegalStateException(
						"Can only be applied to Forms or FormComponents");
				}
			}
		}
		super.beforeRender(context);
	}

}