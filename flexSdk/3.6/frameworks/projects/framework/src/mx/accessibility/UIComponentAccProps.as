////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.accessibility
{

import flash.accessibility.Accessibility;
import flash.accessibility.AccessibilityProperties;
import flash.events.Event;
import flash.system.ApplicationDomain;

import mx.accessibility.AccImpl;
import mx.core.UIComponent;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  UIComponentAccProps is a subclass of AccessibilityProperties
 *  for use by various UIComponents.
 *  It is used to provide accessibility to Form, ToolTip, and Error ToolTip.
 */
public class UIComponentAccProps extends AccessibilityProperties
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
	 *  Enables accessibility in the UIComponent class.
	 * 
	 *  <p>This method is called by application startup code
	 *  that is autogenerated by the MXML compiler.
	 *  Afterwards, when instances of UIComponent are initialized,
	 *  their <code>accessibilityProperties</code> property
	 *  will be set to an instance of this class.</p>
     */
    public static function enableAccessibility():void
    {
        UIComponent.createAccessibilityImplementation =
            createAccessibilityImplementation;
    }

    /**
     *  @private
	 *  Creates a UIComponent's AccessibilityProperties object.
	 *  This method is called from UIComponent's
	 *  initializeAccessibility() method.
     */
    mx_internal static function createAccessibilityImplementation(
                                            component:UIComponent):void
    {
        component.accessibilityProperties =
            new UIComponentAccProps(component);
    }
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *
     *  @param master The UIComponent instance that this
     *  AccessibilityProperties instance is making accessible.
     */
    public function UIComponentAccProps(component:UIComponent)
    {
        super();

        master = component;
        
        if (component.accessibilityProperties)
        {
            silent = component.accessibilityProperties.silent;
            
            forceSimple = component.accessibilityProperties.forceSimple;
            
            noAutoLabeling = component.accessibilityProperties.noAutoLabeling;
            
            if (component.accessibilityProperties.name)
                name = component.accessibilityProperties.name;
            
            if (component.accessibilityProperties.description)
                description = component.accessibilityProperties.description;
            
            if (component.accessibilityProperties.shortcut)
                shortcut = component.accessibilityProperties.shortcut;
        }
        
        var scrollBarClass:Class = Class(AccImpl.getDefinition("mx.controls.scrollClasses.ScrollBar", master.moduleFactory));        
        if (scrollBarClass && master is scrollBarClass)
        {
            silent = true;
            return;
        }

        var formItemLabelClass:Class = Class(AccImpl.getDefinition("mx.controls.FormItemLabel", master.moduleFactory));        
        if (formItemLabelClass && master is formItemLabelClass)
        {
            name = AccImpl.getFormName(master);
            silent = true;
        }
        else
        {
            var formName:String = AccImpl.getFormName(master);

            if (formName && formName.length != 0)
                name = formName + name;  

            if (master.toolTip && master.toolTip.length != 0)
                if (!component.accessibilityProperties || (component.accessibilityProperties && !component.accessibilityProperties.name))
            {
                oldToolTip = " " + master.toolTip;
                name += oldToolTip;
            }

            if (master.errorString && master.errorString.length != 0)
            {
                oldErrorString = " " + master.errorString;
                name += oldErrorString;
            }

            master.addEventListener("toolTipChanged", eventHandler);
            master.addEventListener("errorStringChanged", eventHandler);
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var oldToolTip:String;
    
    /**
     *  @private
     */
    private var oldErrorString:String;
    
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  master
    //----------------------------------

    /**
     *  A reference to the UIComponent itself.
     */
    protected var master:UIComponent;
    
    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  Generic event handler.
     *  All UIComponentAccProps subclasses must implement this
     *  to listen for events from its master component. 
     */
    protected function eventHandler(event:Event):void
    {
        var pos:int;

        switch (event.type)
        {
            case "errorStringChanged":
            {
                if (name && name.length != 0 && oldErrorString)
                {
                    pos = name.indexOf(oldErrorString);
                    if (pos != -1)
                    {
                        name = name.substring(0, pos) +
                               name.substring(pos + oldErrorString.length);
                    }
                    oldErrorString = null;
                }

                if (master.errorString && master.errorString.length != 0)
                {
                    if (!name)
                        name = "";

                    oldErrorString = " " + master.errorString;
                    name += oldErrorString;
                }

                Accessibility.updateProperties();
				break;
            }

            case "toolTipChanged":
            {
                if (name && name.length != 0 && oldToolTip)
                {
                    pos = name.indexOf(oldToolTip);
                    if (pos != -1)
                    {
                        name = name.substring(0, pos) +
                               name.substring(pos + oldToolTip.length);
                    }
                    oldToolTip = null;
                }

                if (master.toolTip && master.toolTip.length != 0)
                {
                    if (!master.accessibilityProperties || (master.accessibilityProperties && !master.accessibilityProperties.name))
                    {
                        if (!name)
                            name = "";

                        oldToolTip = " " + master.toolTip;
                        name += oldToolTip;
                    }
                }

                Accessibility.updateProperties();
				break;
            }
        }
    }
}

}
