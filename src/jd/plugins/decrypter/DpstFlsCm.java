//    jDownloader - Downloadmanager
//    Copyright (C) 2009  JD-Team support@jdownloader.org
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

package jd.plugins.decrypter;

import java.util.ArrayList;

import jd.PluginWrapper;
import jd.controlling.ProgressController;
import jd.parser.Regex;
import jd.plugins.CryptedLink;
import jd.plugins.DecrypterPlugin;
import jd.plugins.DownloadLink;
import jd.plugins.PluginForDecrypt;

@DecrypterPlugin(revision = "$Revision: 12119 $", interfaceVersion = 2, names = { "depositfiles.com" }, urls = { "http://?[\\w\\.]*?.depositfiles\\.com/([a-z]+/)?folders/.+" }, flags = { 0 })
public class DpstFlsCm extends PluginForDecrypt {

    public DpstFlsCm(PluginWrapper wrapper) {
        super(wrapper);
        // TODO Auto-generated constructor stub
    }

    @Override
    public ArrayList<DownloadLink> decryptIt(CryptedLink parameter, ProgressController progress) throws Exception {
        ArrayList<DownloadLink> decryptedLinks = new ArrayList<DownloadLink>();
        String url = parameter.toString();
        int pagecount = 1;
        String id = new Regex(url, "folders/(.+)").getMatch(0);
        url = "http://depositfiles.com/de/folders/" + id;
        // Get Pagecount //
        if (url.contains("page")) url = url.split("\\?")[0];
        br.getPage(url);
        if (br.containsHTML("\\&gt;\\&gt;\\&gt;")) pagecount = Integer.parseInt(br.getRegex("<a href=\\\"/de/folders/[0-9A-Z]+\\?page=[0-9]+\\\">([0-9]+)</a>\\s+<a href=\\\"/de/folders/[0-9A-Z]+\\?page=[0-9]+\\\">&gt;&gt;&gt;</a>").getMatch(0));
        //

        progress.setRange(pagecount * 18);

        for (int x = 1; x <= pagecount; x++) {
            br.getPage(url + "?page=" + x + "&format=text");
            String[] finalLinks = br.getRegex("(http://depositfiles.com/files/[0-9a-z]+)").getColumn(0);
            for (String data : finalLinks) {
                decryptedLinks.add(createDownloadlink(data));
                progress.increase(1);
            }
        }

        return decryptedLinks;
    }

}
