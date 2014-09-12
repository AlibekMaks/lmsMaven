package arta.common.html.util;

import arta.auth.logic.AuthorizationMessages;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.StringTransform;
import arta.javascript.JavaScriptMessages;
import java.io.PrintWriter;
import java.util.Properties;


public class JScript {

    public static void printSelectAllFunction(PrintWriter pw, String prefix, String functionName){
        pw.print("function "+functionName+"(state){");
        pw.print("var n="+prefix.length()+";");
        pw.print("for (var i=0; i<document.all.length; i++){");
        pw.print("if ((document.all[i].name+\"\").substring(0,n)==\""+prefix+"\"){");
        pw.print("document.all[i].checked=state;");
        pw.print("}");
        pw.print("}");
        pw.print("}");
    }

    public static void myPrintSelectAllFunction(PrintWriter pw, String prefix, String mainCheckboxName, String formName){
        pw.print("\n");

        pw.print("function select_all(checked) {\n");
        pw.print("    var inputs = document.getElementsByTagName('input');\n");
        pw.print("    for(var i=0; i<inputs.length; i++){\n");
        pw.print("        if(inputs[i].type==\"checkbox\" && (inputs[i].name).substring(0,"+prefix.length()+")==\""+prefix+"\")\n");
        pw.print("            inputs[i].checked=checked;\n");
        pw.print("    }\n");
        pw.print("}\n");

        pw.print("\n");

        pw.print("function all_is_selected() {\n");
        pw.print("    var inputs = document.getElementsByTagName('input');\n");
        pw.print("    for(var i=0; i<inputs.length; i++){\n");
        pw.print("        if(inputs[i].type==\"checkbox\" && (inputs[i].name).substring(0,"+prefix.length()+")==\""+prefix+"\"){\n");
        pw.print("            if(inputs[i].checked==false) { return false; }\n");
        pw.print("        }\n");
        pw.print("    }\n");
        pw.print("    return true;\n");
        pw.print("}\n");

        pw.print("\n");

        pw.print("function change_a(change){\n");
        pw.print("    if(change.name==\""+mainCheckboxName+"\"){\n");
        pw.print("        select_all("+formName+".all.checked);\n");
        pw.print("    } else {\n");
        pw.print("        "+formName+".all.checked = all_is_selected();\n");
        pw.print("    }\n");

        pw.print("}\n");
    }

    public static void myPrintSelectAllFunction2(PrintWriter pw, String prefix, String mainCheckboxName, String formName){
        pw.print("\n");

        pw.print("function select_all(checked) {\n");
        pw.print("    var inputs = document.getElementsByTagName('input');\n");
        pw.print("    for(var i=0; i<inputs.length; i++){\n");
        pw.print("        if(inputs[i].type==\"checkbox\" && (inputs[i].name).substring(0,"+prefix.length()+")==\""+prefix+"\"){\n");
        pw.print("            inputs[i].checked=checked;\n");
        pw.print("            onChangeChb(inputs[i]);\n");
        pw.print("        }\n");
        pw.print("    }\n");
        pw.print("}\n");

        pw.print("\n");

        pw.print("function all_is_selected() {\n");
        pw.print("    var inputs = document.getElementsByTagName('input');\n");
        pw.print("    for(var i=0; i<inputs.length; i++){\n");
        pw.print("        if(inputs[i].type==\"checkbox\" && (inputs[i].name).substring(0,"+prefix.length()+")==\""+prefix+"\"){\n");
        pw.print("            if(inputs[i].checked==false) { return false; }\n");
        pw.print("        }\n");
        pw.print("    }\n");
        pw.print("    return true;\n");
        pw.print("}\n");

        pw.print("\n");

        pw.print("function change_a(change){\n");
        pw.print("    if(change.name==\""+mainCheckboxName+"\"){\n");
        pw.print("        select_all("+formName+".all.checked);\n");
        pw.print("    } else {\n");
        pw.print("        "+formName+".all.checked = all_is_selected();\n");
        pw.print("    }\n");

        pw.print("}\n");
    }

    public static void writeSetupFunction(PrintWriter pw, int lang){

        pw.print("<script language=\"JavaScript\">");
        pw.print("<!--\n");
        pw.print("    function setup() {\n");
        pw.print("      var inp = document.getElementById('date1');\n");
        pw.print("      inp.onkeyup = keyUP;");
//        pw.print("        //Set up the date parsers\n");
//        pw.print("        var dateParser = new DateParser(\"dd-MM-yyyy HH:mm\");\n");
//        pw.print("    \n");
//        pw.print("        //Set up the InputMask    \n");
//        pw.print("        var numericMask = new InputMask(JST_MASK_NUMBERS, \"numeric\");\n");
//        pw.print("        var decimalMask = new InputMask(JST_MASK_DECIMAL, \"decimal\");\n");
//        pw.print("        var upperMask = new InputMask(fieldBuilder.upperAll(), \"upper\");\n");
//        pw.print("        var lowerMask = new InputMask(fieldBuilder.lowerAll(), \"lower\");\n");
//        pw.print("        var capitalizeMask = new InputMask(fieldBuilder.capitalizeAll(), \"capital\");\n");
//        pw.print("        var dateMask = new InputMask(JST_MASK_DATE, \"date\");\n");
//        pw.print("        var dateTimeMask = new InputMask(JST_MASK_DATE_TIME, \"dateTime\");\n");
//        pw.print("        var dateTimeSecMask = new InputMask(JST_MASK_DATE_TIME_SEC, \"dateTimeSec\");\n");
//        pw.print("        var phoneMask = new InputMask(\"(##) ####-####\", \"phone\");\n");
//        pw.print("        var custom1Mask = new InputMask([fieldBuilder.upperLetters(2, 6), fieldBuilder.literal(\"/\"), fieldBuilder.inputNumbers(1, 3)], \"custom1\");\n");
//        pw.print("        var custom2Mask = new InputMask(\"AA.UU.LL.CC-##\", \"custom2\");\n");
//        pw.print("        var custom3Mask = new InputMask(\"AAAA####\", \"custom3\");\n");
//        pw.print("        var custom4Mask = new InputMask(\"AAAA####-\", \"custom4\");\n");
//        pw.print("    \n");
//        pw.print("        //Set up the NumberMasks\n");
//        pw.print("        var decimalSeparator = \",\";\n");
//        pw.print("        var groupSeparator = \".\";\n");
//        pw.print("        \n");
//        pw.print("        var numParser1 = new NumberParser(0, decimalSeparator, groupSeparator, true);\n");
//        pw.print("        var numMask1 = new NumberMask(numParser1, \"num1\");\n");
//        pw.print("\n");
//        pw.print("        var numParser2 = new NumberParser(-1, decimalSeparator, groupSeparator, true);\n");
//        pw.print("        numParser2.negativeParenthesis = true;\n");
//        pw.print("        var numMask2 = new NumberMask(numParser2, \"num2\");\n");
//        pw.print("        numMask2.leftToRight = true;\n");
//        pw.print("    \n");
//        pw.print("        var numParser3 = new NumberParser(3, decimalSeparator, groupSeparator, true);\n");
//        pw.print("        var numMask3 = new NumberMask(numParser3, \"num3\", 6);\n");
//        pw.print("        numMask3.allowNegative = false;\n");
//        pw.print("        numMask3.leftToRight = true;\n");
//        pw.print("    \n");
//        pw.print("        var numParser4 = new NumberParser(2, decimalSeparator, groupSeparator, true);\n");
//        pw.print("        numParser4.currencySymbol = \"R$\"\n");
//        pw.print("        numParser4.useCurrency = true;\n");
//        pw.print("        numParser4.negativeParenthesis = true;\n");
//        pw.print("        numParser4.currencyInside = true;\n");
//        pw.print("        var numMask4 = new NumberMask(numParser4, \"num4\", 6);\n");
        pw.print("    \n");
        pw.print("        //Set up the DateMasks\n");
        pw.print("        var errorMessage = \"" + MessageManager.readSimpleMessage(lang, JavaScriptMessages.ERROR_IN_THE_DATE) + "\";\n");
        pw.print("    \n");
        pw.print("        var dateMask1 = new DateMask(\"dd-MM-yyyy\", \"date1\");\n");
        pw.print("        dateMask1.validationMessage = errorMessage;\n");
        pw.print("    \n");
//        pw.print("        var dateMask2 = new DateMask(\"dd/MM/yyyy HH:mm\", \"date2\");\n");
//        pw.print("        dateMask2.validationMessage = errorMessage;\n");
//        pw.print("    \n");
//        pw.print("        var dateParser3 = new DateParser(\"dd/MM/yyyy HH:mm\", false, new Date());\n");
//        pw.print("        var dateMask3 = new DateMask(dateParser3, \"date3\");\n");
//        pw.print("        dateMask3.validationMessage = errorMessage;\n");
//        pw.print("    \n");
//        pw.print("        var dateParser4 = new DateParser(\"HH:mm:ss\");\n");
//        pw.print("        var dateMask4 = new DateMask(dateParser4, \"date4\");\n");
//        pw.print("        dateMask4.validationMessage = errorMessage;\n");
//        pw.print("    \n");
//        pw.print("        //Set up the SizeLimits\n");
//        pw.print("        function updateLimit4(control, size, max, left) {\n");
//        pw.print("            var gauge = getObject(\"limit4Gauge\");\n");
//        pw.print("            var width = Math.round(size * 100 / max) + \"%\";\n");
//        pw.print("            gauge.style.width = width;\n");
//        pw.print("            gauge.style.backgroundColor = (width == '100%' ? 'red' : 'blue');\n");
//        pw.print("            gauge.innerHTML = width;\n");
//        pw.print("        }\n");
//        pw.print("        new SizeLimit(\"limit1\", 50, \"limit1Out\");\n");
//        pw.print("        new SizeLimit(\"limit2\", 50, \"limit2Out\", \"${size} / ${max}\");\n");
//        pw.print("        new SizeLimit(\"limit3\", 50, \"limit3Out\", \"You typed ${size} characters of a total of ${max}.<br>You have ${left} more characters to type.<br>This field only accepts alphanumeric characters and spaces.\");\n");
//        pw.print("        new InputMask(new Input(JST_CHARS_ALPHA + JST_CHARS_WHITESPACE), \"limit3\");\n");
//        pw.print("        var limit = new SizeLimit(\"limit4\", 50);\n");
//        pw.print("        limit.updateFunction = updateLimit4;\n");
//        pw.print("        limit.update();\n");
        pw.print("    }\n");
        pw.print("//-->\n");
        pw.print("</script>");



    }



}
