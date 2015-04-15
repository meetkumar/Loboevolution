package org.lobobrowser.html.domimpl;

import java.util.ArrayList;

import org.lobobrowser.html.FormInput;
import org.lobobrowser.html.HtmlAttributeProperties;
import org.lobobrowser.html.dombl.InputContext;
import org.lobobrowser.html.w3c.HTMLCollection;
import org.lobobrowser.html.w3c.HTMLElement;
import org.lobobrowser.html.w3c.HTMLOptionsCollection;
import org.lobobrowser.html.w3c.HTMLSelectElement;
import org.lobobrowser.html.w3c.ValidityState;
import org.mozilla.javascript.Function;
import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;


/**
 * The Class HTMLSelectElementImpl.
 */
public class HTMLSelectElementImpl extends HTMLBaseInputElement implements
		HTMLSelectElement {
	
	/**
	 * Instantiates a new HTML select element impl.
	 *
	 * @param name the name
	 */
	public HTMLSelectElementImpl(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#add(org.lobobrowser.html.w3c.HTMLElement, org.lobobrowser.html.w3c.HTMLElement)
	 */
	public void add(HTMLElement element, HTMLElement before)
			throws DOMException {
		this.insertBefore(element, before);
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#getLength()
	 */
	public int getLength() {
		return this.getOptions().getLength();
	}

	/** The multiple state. */
	private Boolean multipleState = null;

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#getMultiple()
	 */
	public boolean getMultiple() {
		Boolean m = this.multipleState;
		if (m != null) {
			return m.booleanValue();
		}
		return this.getAttributeAsBoolean("multiple");
	}

	/** The options. */
	private HTMLOptionsCollection options;

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#getOptions()
	 */
	public HTMLOptionsCollection getOptions() {
		synchronized (this) {
			if (this.options == null) {
				this.options = new HTMLOptionsCollectionImpl(this);
			}
			return this.options;
		}
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#getSelectedIndex()
	 */
	public int getSelectedIndex() {
		InputContext ic = this.inputContext;
		if (ic != null) {
			return ic.getSelectedIndex();
		} else {
			return this.deferredSelectedIndex;
		}
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#getSize()
	 */
	public int getSize() {
		InputContext ic = this.inputContext;
		if (ic != null) {
			return ic.getVisibleSize();
		} else {
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#getType()
	 */
	public String getType() {
		return this.getMultiple() ? "select-multiple" : "select-one";
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#remove(int)
	 */
	public void remove(int index) {
		try {
			this.removeChild(this.getOptions().item(index));
		} catch (DOMException de) {
			this.warn("remove(): Unable to remove option at index " + index
					+ ".", de);
		}
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#setLength(int)
	 */
	public void setLength(int length) throws DOMException {
		this.getOptions().setLength(length);
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#setMultiple(boolean)
	 */
	public void setMultiple(boolean multiple) {
		boolean prevMultiple = this.getMultiple();
		this.multipleState = Boolean.valueOf(multiple);
		if (prevMultiple != multiple) {
			this.informLayoutInvalid();
		}
	}

	/** The deferred selected index. */
	private int deferredSelectedIndex = -1;

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#setSelectedIndex(int)
	 */
	public void setSelectedIndex(int selectedIndex) {
		this.setSelectedIndexImpl(selectedIndex);
		HTMLOptionsCollection options = this.getOptions();
		int length = options.getLength();
		for (int i = 0; i < length; i++) {
			HTMLOptionElementImpl option = (HTMLOptionElementImpl) options
					.item(i);
			option.setSelectedImpl(i == selectedIndex);
		}
	}

	/**
	 * Sets the selected index impl.
	 *
	 * @param selectedIndex the new selected index impl
	 */
	void setSelectedIndexImpl(int selectedIndex) {
		InputContext ic = this.inputContext;
		if (ic != null) {
			ic.setSelectedIndex(selectedIndex);
		} else {
			this.deferredSelectedIndex = selectedIndex;
		}
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#setSize(int)
	 */
	public void setSize(int size) {
		InputContext ic = this.inputContext;
		if (ic != null) {
			ic.setVisibleSize(size);
		}
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.domimpl.HTMLElementImpl#getFormInputs()
	 */
	protected FormInput[] getFormInputs() {
		// Needs to be overriden for forms to submit.
		InputContext ic = this.inputContext;
		String[] values = ic == null ? null : ic.getValues();
		if (values == null) {
			String value = this.getValue();
			values = value == null ? null : new String[] { value };
			if (values == null) {
				return null;
			}
		}
		String name = this.getName();
		if (name == null) {
			return null;
		}
		ArrayList<FormInput> formInputs = new ArrayList<FormInput>();
		for (int i = 0; i < values.length; i++) {
			formInputs.add(new FormInput(name, values[i]));
		}
		return (FormInput[]) formInputs.toArray(FormInput.EMPTY_ARRAY);
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.domimpl.HTMLBaseInputElement#resetInput()
	 */
	public void resetInput() {
		InputContext ic = this.inputContext;
		if (ic != null) {
			ic.resetInput();
		}
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.domimpl.HTMLBaseInputElement#setInputContext(org.lobobrowser.html.dombl.InputContext)
	 */
	public void setInputContext(InputContext ic) {
		super.setInputContext(ic);
		if (ic != null) {
			ic.setSelectedIndex(this.deferredSelectedIndex);
		}
	}

	/** The onchange. */
	private Function onchange;

	/**
	 * Gets the onchange.
	 *
	 * @return the onchange
	 */
	public Function getOnchange() {
		return this.getEventFunction(this.onchange, "onchange");
	}

	/**
	 * Sets the onchange.
	 *
	 * @param value the new onchange
	 */
	public void setOnchange(Function value) {
		this.onchange = value;
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#getAutofocus()
	 */
	@Override
	public boolean getAutofocus() {
		String auto = this.getAttribute(HtmlAttributeProperties.AUTOFOCUS);
		return HtmlAttributeProperties.AUTOFOCUS.equalsIgnoreCase(auto);
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#setAutofocus(boolean)
	 */
	@Override
	public void setAutofocus(boolean autofocus) {
		this.setAttribute(HtmlAttributeProperties.AUTOFOCUS, autofocus ? HtmlAttributeProperties.AUTOFOCUS : null);
		
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#item(int)
	 */
	@Override
	public Object item(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#namedItem(java.lang.String)
	 */
	@Override
	public Object namedItem(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#add(org.lobobrowser.html.w3c.HTMLElement)
	 */
	@Override
	public void add(HTMLElement element) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#add(org.lobobrowser.html.w3c.HTMLElement, int)
	 */
	@Override
	public void add(HTMLElement element, int before) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#getSelectedOptions()
	 */
	@Override
	public HTMLCollection getSelectedOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#getWillValidate()
	 */
	@Override
	public boolean getWillValidate() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#getValidity()
	 */
	@Override
	public ValidityState getValidity() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#getValidationMessage()
	 */
	@Override
	public String getValidationMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#checkValidity()
	 */
	@Override
	public boolean checkValidity() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#setCustomValidity(java.lang.String)
	 */
	@Override
	public void setCustomValidity(String error) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.lobobrowser.html.w3c.HTMLSelectElement#getLabels()
	 */
	@Override
	public NodeList getLabels() {
		// TODO Auto-generated method stub
		return null;
	}
}