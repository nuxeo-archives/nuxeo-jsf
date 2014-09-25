/*
 * (C) Copyright 2014 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Anahide Tchertchian
 */
package org.nuxeo.theme.jsf.renderer;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;

import org.nuxeo.ecm.platform.ui.web.util.BaseURL;
import org.nuxeo.theme.html.Utils;
import org.nuxeo.theme.html.ui.Resources;

import com.sun.faces.renderkit.html_basic.ScriptStyleBaseRenderer;

/**
 * @since 5.9.6
 */
public class ThemeStylesRenderer extends ScriptStyleBaseRenderer {

    @Override
    protected void startElement(ResponseWriter writer, UIComponent component)
            throws IOException {
    }

    @Override
    protected void endElement(ResponseWriter writer) throws IOException {
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
            throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        final ExternalContext externalContext = context.getExternalContext();

        Map<String, String> params = new HashMap<String, String>();

        Map<String, Object> requestMap = externalContext.getRequestMap();
        URL themeUrl = (URL) requestMap.get("org.nuxeo.theme.url");
        final Map<String, Object> attributes = component.getAttributes();

        String contextPath = BaseURL.getContextPath();
        params.put("contextPath", contextPath);
        params.put("themeUrl", themeUrl.toString());
        params.put("path", contextPath);
        params.put("ignoreLocal", (String) attributes.get("ignoreLocal"));

        String basePath = contextPath + "/site";
        params.put("basepath", basePath);

        Boolean virtualHosting = Utils.isVirtualHosting((HttpServletRequest) externalContext.getRequest());
        writer.write(Resources.render(params, virtualHosting));
    }

}