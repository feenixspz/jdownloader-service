//jDownloader - Downloadmanager
//Copyright (C) 2009  JD-Team support@jdownloader.org
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jd.plugins.decrypter;

import java.util.ArrayList;

import jd.PluginWrapper;
import jd.controlling.ProgressController;
import jd.parser.html.Form;
import jd.plugins.CryptedLink;
import jd.plugins.DecrypterPlugin;
import jd.plugins.DownloadLink;
import jd.plugins.LinkStatus;
import jd.plugins.PluginException;
import jd.plugins.PluginForDecrypt;

@DecrypterPlugin(revision = "$Revision: 12369 $", interfaceVersion = 2, names = { "ikr.me" }, urls = { "http://[\\w\\.]*?ikr\\.me/[A-Za-z0-9]{2}-?" }, flags = { 0 })
public class KrM extends PluginForDecrypt {

    public KrM(PluginWrapper wrapper) {
        super(wrapper);
    }

    // @Override
    public ArrayList<DownloadLink> decryptIt(CryptedLink param, ProgressController progress) throws Exception {
        ArrayList<DownloadLink> decryptedLinks = new ArrayList<DownloadLink>();
        String linkurl = null;
        String parameter = param.toString();
        br.setFollowRedirects(false);
        br.getPage(parameter);

        if (!br.containsHTML("Kod.*?ne postoji u iOboru")) {
            Form form = br.getForm(0);

            boolean success = false;
            for (int i = 0; i < 5; i++) {
                if (form != null && form.hasInputFieldByName("key")) {
                    logger.info("pw protected link");
                    String password = getUserInput(null, param);
                    form.put("key", password);
                    br.submitForm(form);
                    form = br.getForm(0);
                } else {
                    success = true;
                    break;
                }
            }

            if (!success) { throw new PluginException(LinkStatus.ERROR_CAPTCHA); }

            linkurl = br.getRedirectLocation();

            if (linkurl == null) {
                linkurl = br.getRegex("<h2>.*?</h2><blockquote><h3><a href='(.*?)'.*?><b>.*?</b></a>").getMatch(0);
            }

            if (linkurl == null) return null;

            decryptedLinks.add(createDownloadlink(linkurl));
        }

        return decryptedLinks;
    }

    // @Override

}
