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

package jd.plugins.optional.customizer;

import org.appwork.utils.swing.table.ExtTable;

public class CustomizerTable extends ExtTable<CustomizeSetting> {

    private static final long serialVersionUID = 2767338885884748758L;

    public CustomizerTable() {
        super(new CustomizerTableModel(), "customizer");

        setRowHeight(22);
    }

    @Override
    public CustomizerTableModel getModel() {
        return (CustomizerTableModel) super.getModel();
    }

}