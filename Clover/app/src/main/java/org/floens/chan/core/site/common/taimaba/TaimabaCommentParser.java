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
package org.floens.chan.core.site.common.taimaba;

import org.floens.chan.core.site.parser.CommentParser;
import org.floens.chan.core.site.parser.StyleRule;

import java.util.regex.Pattern;

import static org.floens.chan.utils.AndroidUtils.sp;

public class TaimabaCommentParser extends CommentParser {
    public TaimabaCommentParser() {
        addDefaultRules();
        setQuotePattern(Pattern.compile(".*#(\\d+)"));
        setFullQuotePattern(Pattern.compile("/(\\w+)/thread/(\\d+)#(\\d+)"));
        rule(StyleRule.tagRule("strike").strikeThrough());
        rule(StyleRule.tagRule("pre").monospace().size(sp(12f)));
        rule(StyleRule.tagRule("blockquote").cssClass("unkfunc").color(StyleRule.Color.INLINE_QUOTE).linkify());
    }
}