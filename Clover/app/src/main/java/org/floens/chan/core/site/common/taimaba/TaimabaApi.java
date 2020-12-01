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

import android.util.JsonReader;

import org.floens.chan.core.model.Post;
import org.floens.chan.core.model.PostHttpIcon;
import org.floens.chan.core.model.PostImage;
import org.floens.chan.core.site.SiteEndpoints;
import org.floens.chan.core.site.common.CommonSite;
import org.floens.chan.core.site.parser.ChanReaderProcessingQueue;

import org.jsoup.parser.Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;

import static org.floens.chan.core.site.SiteEndpoints.makeArgument;

public class TaimabaApi extends CommonSite.CommonApi {
    public TaimabaApi(CommonSite commonSite) {
        super(commonSite);
    }

    @Override
    public void loadThread(JsonReader reader, ChanReaderProcessingQueue queue) throws Exception {
        reader.beginObject();
        // Page object
        while (reader.hasNext()) {
            String key = reader.nextName();
            if (key.equals("posts")) {
                reader.beginArray();
                // Thread array
                while (reader.hasNext()) {
                    // Thread object
                    readPostObject(reader, queue);
                }
                reader.endArray();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    @Override
    public void loadCatalog(JsonReader reader, ChanReaderProcessingQueue queue) throws Exception {
        reader.beginArray(); // Array of pages

        while (reader.hasNext()) {
            reader.beginObject(); // Page object

            while (reader.hasNext()) {
                if (reader.nextName().equals("threads")) {
                    reader.beginArray(); // Threads array

                    while (reader.hasNext()) {
                        readPostObject(reader, queue);
                    }

                    reader.endArray();
                } else {
                    reader.skipValue();
                }
            }

            reader.endObject();
        }

        reader.endArray();
    }

    @Override
    public void readPostObject(JsonReader reader, ChanReaderProcessingQueue queue) throws Exception {
        Post.Builder builder = new Post.Builder();
        builder.board(queue.getLoadable().board);

        SiteEndpoints endpoints = queue.getLoadable().getSite().endpoints();

        // File
        String fileExt = null;
        int fileWidth = 0;
        int fileHeight = 0;
        long fileSize = 0;
        boolean fileSpoiler = false;
        String fileName = null;

		/* prevent API parse error

           resto is not available on opening board overview the first time
           so, we manually set the opId to 0, builder.op to true and builder.opId to 0 */
        int opId = 0;
        builder.op(opId == 0);
        builder.opId(0);

        String postcom = null;

        List<PostImage> files = new ArrayList<>();

        // Country flag
        String countryCode = null;
        String trollCountryCode = null;
        String countryName = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();

            switch (key) {
				case "no":
                    builder.id(reader.nextInt());
                    break;
                case "resto":
                    opId = reader.nextInt();
                    builder.op(opId == 0);
                    builder.opId(opId);
                    break;
                case "sticky":
                    builder.sticky(reader.nextInt() == 1);
                    break;
                case "closed":
                    builder.closed(reader.nextInt() == 1);
                    break;
                case "time":
                    builder.setUnixTimestampSeconds(reader.nextLong());
                    break;
               case "name":
                    builder.name(reader.nextString());
                    break;
                case "trip":
                    builder.tripcode(reader.nextString());
                    break;
                case "id":
                    builder.posterId(reader.nextString());
                    break;
                case "sub":
                    builder.subject(reader.nextString());
                    break;
                case "com":
                    postcom = reader.nextString();
                    postcom = postcom.replaceAll(">(.*+)", "<blockquote class=\"unkfunc\">&gt;$1</blockquote>");
                    postcom = postcom.replaceAll("<blockquote class=\"unkfunc\">&gt;>(\\d+)</blockquote>", "<a href=\"#$1\">&gt;&gt;$1</a>");
                    postcom = postcom.replaceAll("\n", "<br/>");
                    postcom = postcom.replaceAll("(?i)\\[b\\](.*?)\\[/b\\]", "<b>$1</b>");
                    postcom = postcom.replaceAll("(?i)\\[\\*\\*\\](.*?)\\[/\\*\\*\\]", "<b>$1</b>");
                    postcom = postcom.replaceAll("(?i)\\[i\\](.*?)\\[/i\\]", "<i>$1</i>");
                    postcom = postcom.replaceAll("(?i)\\[\\*\\](.*?)\\[/\\*\\]", "<i>$1</i>");
                    postcom = postcom.replaceAll("(?i)\\[spoiler\\](.*?)\\[/spoiler\\]", "<span class=\"spoiler\">$1</span>");
                    postcom = postcom.replaceAll("(?i)\\[%\\](.*?)\\[/%\\]", "<span class=\"spoiler\">$1</span>");
                    postcom = postcom.replaceAll("(?i)\\[s\\](.*?)\\[/s\\]", "<strike>$1</strike>");
                    postcom = postcom.replaceAll("(?i)\\[pre\\](.*?)\\[/pre\\]", "<pre>$1</pre>");
                    postcom = postcom.replaceAll("(?i)\\[sub\\](.*?)\\[/sub\\]", "<pre>$1</pre>");
                    builder.comment(postcom);
                    break;
                case "filename":
                    fileName = reader.nextString();
                    break;
                case "ext":
                    fileExt = reader.nextString().replace(".", "");
                    break;
                case "fsize":
                    fileSize = reader.nextLong();
                    break;
                case "w":
                    fileWidth = reader.nextInt();
                    break;
                case "h":
                    fileHeight = reader.nextInt();
                    break;
                case "country":
                    countryCode = reader.nextString();
                    break;
                case "troll_country":
                    trollCountryCode = reader.nextString();
                    break;
                case "country_name":
                    countryName = reader.nextString();
                    break;
                case "spoiler":
                    fileSpoiler = reader.nextInt() == 1;
                    break;
                case "archived":
                    builder.archived(reader.nextInt() == 1);
                    break;
                case "replies":
                    builder.replies(reader.nextInt());
                    break;
                case "images":
                    builder.images(reader.nextInt());
                    break;
                case "unique_ips":
                    builder.uniqueIps(reader.nextInt());
                    break;
                case "last_modified":
                    builder.lastModified(reader.nextLong());
                    break;
                case "capcode":
                    builder.moderatorCapcode(reader.nextString());
                    break;
                case "extra_files":
                    reader.beginArray();

                    while (reader.hasNext()) {
                        PostImage postImage = readPostImage(reader, builder, endpoints);
                        if (postImage != null) {
                            files.add(postImage);
                        }
                    }

                    reader.endArray();
                    break;
                default:
                    // Unknown/ignored key
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        // The file from between the other values.
        if (fileName != null && fileExt != null) {
            Map<String, String> args = makeArgument("tim", fileName,
                    "ext", fileExt);
            PostImage image = new PostImage.Builder()
                    .originalName(org.jsoup.parser.Parser.unescapeEntities(fileName, false))
                    .thumbnailUrl(endpoints.thumbnailUrl(builder, false, args))
                    .spoilerThumbnailUrl(endpoints.thumbnailUrl(builder, true, args))
                    .imageUrl(endpoints.imageUrl(builder, args))
                    .filename(Parser.unescapeEntities(fileName, false))
                    .extension(fileExt)
                    .imageWidth(fileWidth)
                    .imageHeight(fileHeight)
                    .spoiler(fileSpoiler)
                    .size(fileSize)
                    .build();
            // Insert it at the beginning.
            files.add(0, image);
        }

        builder.images(files);

        if (builder.op) {
            // Update OP fields later on the main thread
            Post.Builder op = new Post.Builder();
            op.closed(builder.closed);
            op.archived(builder.archived);
            op.sticky(builder.sticky);
            op.replies(builder.replies);
            op.images(builder.imagesCount);
            op.uniqueIps(builder.uniqueIps);
            op.lastModified(builder.lastModified);
            queue.setOp(op);
        }

        Post cached = queue.getCachedPost(builder.id);
        if (cached != null) {
            // Id is known, use the cached post object.
            queue.addForReuse(cached);
            return;
        }

        if (countryCode != null && countryName != null) {
            HttpUrl countryUrl = endpoints.icon(builder, "country",
                    makeArgument("country_code", countryCode));
            builder.addHttpIcon(new PostHttpIcon(countryUrl, countryName));
        }

        if (trollCountryCode != null && countryName != null) {
            HttpUrl countryUrl = endpoints.icon(builder, "troll_country",
                    makeArgument("troll_country_code", trollCountryCode));
            builder.addHttpIcon(new PostHttpIcon(countryUrl, countryName));
        }

        queue.addForParse(builder);
    }

    private PostImage readPostImage(JsonReader reader, Post.Builder builder,
                                    SiteEndpoints endpoints) throws IOException {
        reader.beginObject();

        long fileSize = 0;

        String fileExt = null;
        int fileWidth = 0;
        int fileHeight = 0;
        boolean fileSpoiler = false;
        String fileName = null;

        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "fsize":
                    fileSize = reader.nextLong();
                    break;
                case "w":
                    fileWidth = reader.nextInt();
                    break;
                case "h":
                    fileHeight = reader.nextInt();
                    break;
                case "spoiler":
                    fileSpoiler = reader.nextInt() == 1;
                    break;
                case "ext":
                    fileExt = reader.nextString().replace(".", "");
                    break;
                case "filename":
                    fileName = reader.nextString();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }

        reader.endObject();

        if (fileName != null && fileExt != null) {
            Map<String, String> args = makeArgument("tim", fileName,
                    "ext", fileExt);
            return new PostImage.Builder()
                    .originalName(org.jsoup.parser.Parser.unescapeEntities(fileName, false))
                    .thumbnailUrl(endpoints.thumbnailUrl(builder, false, args))
                    .spoilerThumbnailUrl(endpoints.thumbnailUrl(builder, true, args))
                    .imageUrl(endpoints.imageUrl(builder, args))
                    .filename(Parser.unescapeEntities(fileName, false))
                    .extension(fileExt)
                    .imageWidth(fileWidth)
                    .imageHeight(fileHeight)
                    .spoiler(fileSpoiler)
                    .size(fileSize)
                    .build();
        }
        return null;
    }
}