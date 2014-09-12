package arta.common.html.util;

import java.io.PrintWriter;

public class TinyMce {

    /**
     * Размеры компонента буду заданы в процентах
     */
    public static int DIMENSION_TYPE_PERCENT = 0;

    /**
     * Размеры компонента будут заданы в пунктах
     */
    public static int DIMENSION_TYPE_PT = 1;

    /**
     * Размеры компонета буду заданы количеством столбцов и строк
     */
    public static int DIMENSION_TYPE_ROW_COLUMN = 2;

    /**
     * Ширина компонента будет задана впроцентах,
     * высота будет определена количеством рядов
     */
    public static int DIMENSION_TYPE__PERCENT_ROW = 3;

    int lang;

    public TinyMce(int lang) {
        this.lang = lang;
    }

    private boolean pasteImages = false;

    /**
     * Метод инициализирует advanced компонент, но без вставки из буфера
     * и загрузки файлов
     * @param pw PrintWriter с пмощью которого выводится страница
     */
    public void initTinyMce(PrintWriter pw){
        pasteImages = false;
        initializeAdvancedTheme(pw);
    }

    /**
     * Метод инициализирует advanced компонент с вставкой из буфера
     * и подгрузкой файлов
     * @param pw PrintWriter с пмощью которого выводится страница
     * @param tutorID ID пользователя редактирующего некоторый объект на странице
     * @param type тип объекта (Константы в классе Constants)
     * @param testingID ID редактируемого объекта (например, теста)
     * @param signature некоторая подпись уникльная для данного объекта (timestamp времени создания)
     */
    public void initTinyMce(PrintWriter pw, int tutorID, int type, int testingID, String signature){
        pasteImages = true;
        initializeAdvancedTheme(pw);
        pw.print("<script language=\"javascript\">" +
                    "    function pasteImage(){\n" +
                    "        try{ " +
                    "            if (window.clipboardData.getData(\"text\")!=null)\n" +
                    "                return false;\n" +
                    "            var url = document.insertImageApplet.getNewImageURL("+tutorID+", "+type+", "+testingID+", '"+signature+"');\n" +
                    "            if (url==null || url.length==0)\n" +
                    "            return false;\n" +
                    "            tinyMCE.execCommand('mceInsertImageFromClipboard',false,''+url+'');\n" +
                    "        }catch(e){\n" +
                    "            return false;\n" +
                    "        }\n" +
                    "        return true;\n" +
                    "    }" +
                    "</script>");
    }

    private void initializeAdvancedTheme(PrintWriter pw){
        String myButton = "";
        String smiles = "";
        if (pasteImages){
            myButton = ", mybutton";
        } else {
            smiles = ", emotions";
            myButton = ", emotions";
        }
        pw.print("<script language=\"javascript\" type=\"text/javascript\" src=\"jscripts/tiny_mce/tiny_mce.js\"></script>");
        pw.print("<script language=\"javascript\" type=\"text/javascript\" src=\"jscripts/windows/windows.js\"></script>");
        pw.print("<script language=\"javascript\" type=\"text/javascript\">");
        pw.println("tinyMCE.init({\n" +
                "\tmode : \"textareas\",\n" +
                "\ttheme : \"advanced\", \n" +
                "\tmode:  \"specific_textareas\", \n" +
                "\teditor_selector : \"editor\",\n" +
                "\teditor_deselector : \"notEditor\", \n" +
                "\tplugins : \"style,contextmenu,paste,visualchars,nonbreaking,table,flash"+smiles+"\",\n" +
                "\ttheme_advanced_buttons1_add_before : \"fontselect,fontsizeselect\",\n" +
                "\ttheme_advanced_buttons2_add : \"forecolor, backcolor"+myButton+",tablecontrols\",\n" +
                "\ttheme_advanced_toolbar_location : \"top\",\n" +
                "\ttheme_advanced_toolbar_align : \"left\",\n" +
                "\ttheme_advanced_path_location : \"bottom\",\n" +
                "\tcontent_css : \"example_full.css\",\n" +
                "\tfile_browser_callback : \"fileBrowserCallBack\",\n" +
                "\ttheme_advanced_resize_horizontal : false,\n" +
                "\ttheme_advanced_resizing : true,\n" +
                "\tnonbreaking_force_tab : true,\n" +
                "\tapply_source_formatting : true," +
                "\tpaste_auto_cleanup_on_paste : true,\n" +
                "\tpaste_convert_headers_to_strong : false,\n" +
                "\tpaste_strip_class_attributes : \"all\",\n" +
                "\tpaste_remove_spans : false,\n" +
                "\tpaste_remove_styles : false,\n" +
                "\textended_valid_elements :'object[type|allowScriptAccess|allowNetworking|height|width|data],param[name|value|]'  " +
                "});");
        if (pasteImages){
            pw.println("function aaa(){");
            pw.print("try {");
            pw.println(" return pasteImage();");
            pw.print(" } catch (e){}");
            pw.println("}");
        }
        pw.print("</script>");
    }

    /**
     * Метод выводит компонент для редактирования форматрованного текста
     * @param pw PrintWriter с помощью которого выводится страница
     * @param inputName название инпута
     * @param width ширина
     * @param height высота
     * @param type тип задания размеров компонена (см константы класса)
     * @param value значение инпута
     */
    public void writeTinyMceInput(PrintWriter pw, String inputName, int width, int height, int type, String value){
        pw.print("<textarea class=\"editor\" name=\""+inputName+"\" ");
        if (type == DIMENSION_TYPE_PT)
            pw.print(" style=\"width:"+width+"px; height:"+height+"px;\" ");
        if (type == DIMENSION_TYPE_PERCENT)
            pw.print(" style=\"width:"+width+"%; height:"+height+"%;\"");
        if (type == DIMENSION_TYPE_ROW_COLUMN)
            pw.print(" rows=\""+height+"\" cols=\""+width+"\" ");
        if (type == DIMENSION_TYPE__PERCENT_ROW){
            pw.print(" style=\"width:"+width+"%;\" rows="+height+" ");
        }
        pw.print(">");
        if (value != null)
            pw.print(value);
        pw.print("</textarea>");
    }

    public void writeDisabledInput(PrintWriter pw, int width, int height, int type, String value){
        pw.print("<textarea class=\"editor\" readonly ");
        if (type == DIMENSION_TYPE_PT)
            pw.print(" style=\"width:"+width+"px; height:"+height+"px;\" ");
        if (type == DIMENSION_TYPE_PERCENT)
            pw.print(" style=\"width:"+width+"%; height:"+height+"%;\"");
        if (type == DIMENSION_TYPE_ROW_COLUMN)
            pw.print(" rows=\""+height+"\" cols=\""+width+"\" ");
        if (type == DIMENSION_TYPE__PERCENT_ROW){
            pw.print(" style=\"width:"+width+"%;\" rows="+height+" ");
        }
        pw.print(">");
        if (value != null)
            pw.print(value);
        pw.print("</textarea>");
    }

    public StringBuffer getDisabledInput(int width, int height, int type, String value){
        StringBuffer str = new StringBuffer();
        str.append("<textarea class=\"editor\" readonly ");
        if (type == DIMENSION_TYPE_PT)
            str.append(" style=\"width:"+width+"px; height:"+height+"px;\" ");
        if (type == DIMENSION_TYPE_PERCENT)
            str.append(" style=\"width:"+width+"%; height:"+height+"%;\"");
        if (type == DIMENSION_TYPE_ROW_COLUMN)
            str.append(" rows=\""+height+"\" cols=\""+width+"\" ");
        if (type == DIMENSION_TYPE__PERCENT_ROW){
            str.append(" style=\"width:"+width+"%;\" rows="+height+" ");
        }
        str.append(">");
        if (value != null)
            str.append(value);
        str.append("</textarea>");
        return str;
    }

    public StringBuffer getInit(){
        StringBuffer str = new StringBuffer();
        str.append("<script language=\"javascript\" type=\"text/javascript\" src=\"jscripts/tiny_mce/tiny_mce.js\"></script>");
        str.append("<script language=\"javascript\" type=\"text/javascript\">");
        str.append("tinyMCE.init({\n" +
                "\tmode : \"textareas\",\n" +
                "\ttheme : \"simple\", \n" +
                "\tmode:  \"specific_textareas\", \n" +
                "\teditor_selector : \"editor\",\n" +
                "\teditor_deselector : \"notEditor\", \n" +
                "\tdialog_type : \"modal\",\n" +
                "\tplugins : \"contextmenu\"\n" +
                "});");
        str.append("</script>");
        return str;
    }

}
