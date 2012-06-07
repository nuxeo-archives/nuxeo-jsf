/*
 * (C) Copyright 2012 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Anahide Tchertchian
 */
package org.nuxeo.ecm.platform.ui.web.component;

import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.html.HtmlForm;
import javax.faces.context.FacesContext;

/**
 * Override default form component to handled nested forms. Override is needed
 * to handle client id (avoid pre-prending parent naming container id).
 *
 * @since 5.6
 */
public class UINXForm extends HtmlForm {

    protected String nxClientId;

    @Override
    public String getClientId(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }

        // if the clientId is not yet set
        if (this.nxClientId == null) {
            this.nxClientId = getId();
        }
        return this.nxClientId;
    }

    public boolean isNestedInForm(FacesContext context) {
        UIComponent parent = getParent();
        while (parent != null && !(parent instanceof UIForm)) {
            parent = parent.getParent();
        }

        if (parent != null && (parent instanceof UIForm)) {
            return true;
        }

        return false;
    }

    /**
     * Overriden to process decodes even if current form is not submitted, so
     * that a potential nested form is (and not other components).
     */
    public void processDecodes(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }

        // Process this component itself
        decode(context);

        // if we're not the submitted form, don't process children.

        // FIXME: this is what is a problem to submit subforms.
        // Ideally only sub forms should be processed, but some other
        // components may also be needed (UIAliasHolder for instance, as it
        // exposes variables) => would need to find a criterion for it, maybe
        // rely on components marked with org.ajax4jsf.CONTROL_COMPONENTS?

        if (!isSubmitted()) {
            return;
        }

        // Process all facets and children of this component
        Iterator kids = getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            kid.processDecodes(context);
        }

    }

    @Override
    public void setId(String id) {
        super.setId(id);
        this.nxClientId = null;
    }

    @Override
    public Object saveState(FacesContext context) {
        Object[] values = new Object[2];
        values[0] = super.saveState(context);
        values[1] = nxClientId;
        return values;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[]) state;
        super.restoreState(context, values[0]);
        nxClientId = (String) values[1];
    }

}
