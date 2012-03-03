/*
 * (C) Copyright 2006-2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 *
 * $Id: DateTimeWidgetTypeHandler.java 30416 2008-02-21 19:10:37Z atchertchian $
 */

package org.nuxeo.ecm.platform.forms.layout.facelets.plugins;

import java.io.Serializable;
import java.util.Map;
import java.util.TimeZone;

import javax.faces.component.html.HtmlOutputText;
import javax.faces.convert.DateTimeConverter;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.CompositeFaceletHandler;
import javax.faces.view.facelets.ConverterConfig;
import javax.faces.view.facelets.ConverterHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.FaceletHandler;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagAttributes;
import javax.faces.view.facelets.TagConfig;

import org.nuxeo.ecm.platform.forms.layout.api.BuiltinWidgetModes;
import org.nuxeo.ecm.platform.forms.layout.api.Widget;
import org.nuxeo.ecm.platform.forms.layout.api.exceptions.WidgetException;
import org.nuxeo.ecm.platform.forms.layout.facelets.FaceletHandlerHelper;
import org.nuxeo.ecm.platform.forms.layout.facelets.LeafFaceletHandler;
import org.nuxeo.ecm.platform.forms.layout.facelets.TagConfigFactory;
import org.nuxeo.ecm.platform.ui.web.tag.handler.InputDateTimeTagHandler;
import org.richfaces.component.UICalendar;

import com.sun.faces.facelets.tag.jsf.core.ConvertDateTimeHandler;

/**
 * Date time widget
 *
 * @author <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 */
public class DateTimeWidgetTypeHandler extends AbstractWidgetTypeHandler {

    private static final long serialVersionUID = 1495841177711755669L;

    @Override
    public FaceletHandler getFaceletHandler(FaceletContext ctx,
            TagConfig tagConfig, Widget widget, FaceletHandler[] subHandlers)
            throws WidgetException {
        FaceletHandlerHelper helper = new FaceletHandlerHelper(ctx, tagConfig);
        String mode = widget.getMode();
        String widgetId = widget.getId();
        String widgetName = widget.getName();
        String widgetTagConfigId = widget.getTagConfigId();
        TagAttributes attributes;
        if (BuiltinWidgetModes.isLikePlainMode(mode)) {
            // use attributes without id
            attributes = helper.getTagAttributes(widget);
        } else {
            attributes = helper.getTagAttributes(widgetId, widget);
        }
        FaceletHandler leaf = new LeafFaceletHandler();
        if (BuiltinWidgetModes.EDIT.equals(mode)) {
            ComponentConfig config = TagConfigFactory.createComponentConfig(
                    tagConfig, widget.getTagConfigId(), attributes, leaf,
                    UICalendar.COMPONENT_TYPE, null);
            ComponentHandler input = new InputDateTimeTagHandler(config);
            String msgId = helper.generateMessageId(widgetName);
            ComponentHandler message = helper.getMessageComponentHandler(
                    widgetTagConfigId, msgId, widgetId, null);
            FaceletHandler[] handlers = { input, message };
            return new CompositeFaceletHandler(handlers);
        } else {
            // default on text for other modes
            // set time zone by default
            TagAttributes convertAttributes = attributes;
            Map<String, Serializable> properties = widget.getProperties();
            if (properties == null || !properties.containsKey("timeZone")) {
                TagAttribute timeZone = helper.createAttribute("timeZone",
                        TimeZone.getDefault().getID());
                convertAttributes = FaceletHandlerHelper.addTagAttribute(
                        attributes, timeZone);
            }
            ConverterConfig convertConfig = TagConfigFactory.createConverterConfig(
                    tagConfig, widget.getTagConfigId(), convertAttributes,
                    leaf, DateTimeConverter.CONVERTER_ID);
            ConverterHandler convert = new ConvertDateTimeHandler(convertConfig);
            ComponentHandler output = helper.getHtmlComponentHandler(
                    widgetTagConfigId, attributes, convert,
                    HtmlOutputText.COMPONENT_TYPE, null);
            if (BuiltinWidgetModes.PDF.equals(mode)) {
                // add a surrounding p:html tag handler
                // FIXME: migrate to RichFaces
                return output;
                // return helper.getHtmlComponentHandler(widgetTagConfigId,
                // new TagAttributesImpl(new TagAttribute[0]), output,
                // UIHtmlText.class.getName(), null);
            } else {
                return output;
            }
        }
    }
}
