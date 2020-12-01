/*
 * Clover - 4chan browser https://github.com/Floens/Clover/
 * Copyright (C) 2014  Floens
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.floens.chan.core.site;

import android.util.SparseArray;

import org.floens.chan.core.site.sites.chan4.Chan4;
import org.floens.chan.core.site.sites.chan420.Chan420;
import org.floens.chan.core.site.sites.dvach.Dvach;
import org.floens.chan.core.site.sites.lainchan.Lainchan;
import org.floens.chan.core.site.sites.sushichan.Sushichan;
import org.floens.chan.core.site.sites.geechan.Geechan;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry of all sites and url handler we have.
 */
public class SiteRegistry {
    public static final List<SiteUrlHandler> URL_HANDLERS = new ArrayList<>();
    public static final SparseArray<Class<? extends Site>> SITE_CLASSES = new SparseArray<>();

    static {
        URL_HANDLERS.add(Chan4.URL_HANDLER);
        URL_HANDLERS.add(Chan420.URL_HANDLER);
        URL_HANDLERS.add(Lainchan.URL_HANDLER);
        URL_HANDLERS.add(Sushichan.URL_HANDLER);
        URL_HANDLERS.add(Dvach.URL_HANDLER);
        URL_HANDLERS.add(Geechan.URL_HANDLER);
    }

    static {
        // This id-siteclass mapping is used to look up the correct site class at deserialization.
        // This differs from the Site.id() id, that id is used for site instance linking, this is just to
        // find the correct class to use.
        SITE_CLASSES.put(0, Chan4.class);

        //ID: 1 - 8chan was here
        SITE_CLASSES.put(2, Lainchan.class);
        //ID:3 - Arisuchan was here
        SITE_CLASSES.put(4, Sushichan.class);
        SITE_CLASSES.put(5, Dvach.class);
        SITE_CLASSES.put(6, Chan420.class);
        SITE_CLASSES.put(7, Geechan.class);
    }
}
