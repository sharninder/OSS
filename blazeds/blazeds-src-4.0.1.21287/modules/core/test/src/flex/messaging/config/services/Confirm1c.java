/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2008 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/

package flex.messaging.config.services;

import flex.messaging.config.ConfigurationConfirmation;
import flex.messaging.config.MessagingConfiguration;
import flex.messaging.config.ServiceSettings;
import flex.messaging.config.ChannelSettings;
import flex.messaging.config.AdapterSettings;
import flex.messaging.config.DestinationSettings;
import flex.messaging.LocalizedException;

import java.util.List;
import java.util.Iterator;

public class Confirm1c extends ConfigurationConfirmation
{
    protected MessagingConfiguration EXPECTED_VALUE;

    public Confirm1c()
    {
        MessagingConfiguration config = new MessagingConfiguration();

        ChannelSettings fooChannel = new ChannelSettings("foo-channel");
        fooChannel.setUri("/foo");
        fooChannel.setEndpointType("flex.messaging.endpoints.FooEndpoint");
        config.addChannelSettings("foo-channel", fooChannel);

        ServiceSettings fooService = new ServiceSettings("foo-service");
        fooService.setClassName("flex.messaging.services.FooService");
        config.addServiceSettings(fooService);

        AdapterSettings fooAdapter = new AdapterSettings("foo");
        fooAdapter.setClassName("flex.messaging.services.foo.FooAdapter");
        fooAdapter.setDefault(true);
        fooService.addAdapterSettings(fooAdapter);

        fooService.addDefaultChannel(fooChannel);

        DestinationSettings fooDest = new DestinationSettings("foo-dest");
        fooService.addDestinationSettings(fooDest);

        // Destination Channels (from default)
        List defaultChannels = fooService.getDefaultChannels();
        Iterator it = defaultChannels.iterator();
        while (it.hasNext())
        {
            ChannelSettings c = (ChannelSettings)it.next();
            fooDest.addChannelSettings(c);
        }

        // Destination Adapters (from default)
        AdapterSettings defaultAdapter = fooService.getDefaultAdapter();
        if (fooDest.getAdapterSettings() == null && defaultAdapter != null)
        {
            fooDest.setAdapterSettings(defaultAdapter);
        }

        // Destination Properties
        fooDest.addProperty("fooString", "fooValue");

        EXPECTED_VALUE = config;
    }

    public MessagingConfiguration getExpectedConfiguration()
    {
        return EXPECTED_VALUE;
    }

    public LocalizedException getExpectedException()
    {
        return null;
    }
}

