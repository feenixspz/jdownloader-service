//    jDownloader - Downloadmanager
//    Copyright (C) 2010  JD-Team support@jdownloader.org
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jd.plugins.hoster;

import jd.PluginWrapper;
import jd.nutils.encoding.Encoding;
import jd.parser.Regex;
import jd.plugins.DownloadLink;
import jd.plugins.HostPlugin;
import jd.plugins.LinkStatus;
import jd.plugins.PluginException;
import jd.plugins.PluginForHost;
import jd.plugins.DownloadLink.AvailableStatus;

/**
 * @author typek_pb
 */
@HostPlugin(revision = "$Revision: 11604 $", interfaceVersion = 2, names = { "blip.tv" }, urls = { "http://[\\w\\.]*?blip\\.tv/file/\\d+.*" }, flags = { 0 })
public class BlipTv extends PluginForHost {

    public BlipTv(PluginWrapper wrapper) {
        super(wrapper);
    }

    private String dlink = null;

    @Override
    public String getAGBLink() {
        return "http://blip.tv/tos/";
    }

    @Override
    public void handleFree(DownloadLink link) throws Exception {
        requestFileInformation(link);
        if (dlink == null) throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        dl = jd.plugins.BrowserAdapter.openDownload(br, link, dlink, true, 0);
        if (dl.getConnection().getContentType().contains("html")) {
            dl.getConnection().disconnect();
            throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
        }
        dl.startDownload();
    }

    @Override
    public AvailableStatus requestFileInformation(DownloadLink link) throws Exception {
        br.getPage(link.getDownloadURL());
        String filename = br.getRegex("player.setPostsTitle[(]\"(.*?)\"[)]").getMatch(0);
        if (filename == null || filename.trim().length() == 0) throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);

        dlink = new Regex(Encoding.htmlDecode(br.toString()), "player.setPrimaryMediaUrl[(]\"(.*?)\"[)]").getMatch(0);
        if (dlink == null || dlink.trim().length() == 0) throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);

        String ext = new Regex(dlink, "[.]([a-zA-Z0-9]*)[?]").getMatch(0);
        if (ext == null || ext.trim().length() == 0) throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);

        filename = filename.trim();
        link.setFinalFileName(filename + "." + ext);
        br.setFollowRedirects(true);
        try {
            if (!br.openGetConnection(dlink).getContentType().contains("html")) {
                link.setDownloadSize(br.getHttpConnection().getLongContentLength());
                br.getHttpConnection().disconnect();
                return AvailableStatus.TRUE;
            }
        } finally {
            if (br.getHttpConnection() != null) br.getHttpConnection().disconnect();
        }
        throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
    }

    @Override
    public int getMaxSimultanFreeDownloadNum() {
        return -1;
    }

    @Override
    public void reset() {
    }

    @Override
    public void resetPluginGlobals() {
    }

    @Override
    public void resetDownloadlink(DownloadLink link) {
    }
}
