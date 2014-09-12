package arta.common.html.navigation;

import java.io.PrintWriter;


public class PartsHandler {


    int partsCount;
    int currentPart;
    int lang;
    String link;
    String partNumberName;

    /**
     *  Конструктору передается вся информация необходимая для вывода страниц выборки
     * @param partsCount количество страниц выборки
     * @param currentPart номер текущей страницы
     * @param lang язык, на котором вошел пользователь
     * @param link ссылка, для перехода между страницами выборки с учетом всех параметров
     *          кроме номера страницы
     * @param partNumberName имя переменной обозначающей номер выбранной страницы для http запроса
     */
    public PartsHandler(int partsCount, int currentPart, int lang, String link, String partNumberName) {
        this.partsCount = partsCount;
        this.currentPart = currentPart;
        this.lang = lang;
        this.link = link;
        this.partNumberName = partNumberName;
    }

    /**
     * Метод печатает ссылки для переходов между страницами
     * @param pw PrintWriter данной страницы
     */
    public void writeLinks(PrintWriter pw){
        if (partsCount > 1 && link != null){

            pw.print("<table border=0><tr>");
            if (partsCount > 4){
                if (currentPart > 0){
                    pw.print("<td><a  href=\""+link+"&"+partNumberName+"=0\"><image src=\"images/button.left.end.gif\" " +
                            " border=0 width=\"24\" height=\"24\"></a></td>");
                    pw.print("<td><a  href=\""+link+"&"+partNumberName+"="+(currentPart-1)+"\"><image src=\"images/button.left.gif\" " +
                            " border=0 width=\"24\" height=\"24\"></a></td>");
                }
            }

            if (partsCount>4){
                if (currentPart==0){
                    pw.print("<td><a class=\"navSelectedLink\" href=\""+link+"&"+partNumberName+"=0\">1</a></td>");
                    pw.print("<td><a class=\"navLink\" href=\""+link+"&"+partNumberName+"=1\">2</a></td>");
                    pw.print("<td><a class=\"navLink\" href=\""+link+"&"+partNumberName+"=2\">3</a></td>");
                    pw.print("<td class=\"commonTD\">...</td>");
                }else if (currentPart==partsCount-1){
                    pw.print("<td class=\"commonTD\">...</td>");
                    pw.print("<td><a class=\"navLink\" href=\""+link+"&"+partNumberName+"="+(partsCount-3)+"\">"+(partsCount-2)+"</a></td>");
                    pw.print("<td><a class=\"navLink\" href=\""+link+"&"+partNumberName+"="+(partsCount-2)+"\">"+(partsCount-1)+"</a></td>");
                    pw.print("<td><a class=\"navSelectedLink\" href=\""+link+"&"+partNumberName+"="+(partsCount-1)+"\">"+(partsCount)+"</a></td>");
                } else {
                    pw.print("<td class=\"commonTD\">...</td>");
                    pw.print("<td><a class=\"navLink\" href=\""+link+"&"+partNumberName+"="+(currentPart-1)+"\">"+(currentPart)+"</a></td>");
                    pw.print("<td><a class=\"navSelectedLink\" href=\""+link+"&"+partNumberName+"="+(currentPart)+"\">"+(currentPart+1)+"</a></td>");
                    pw.print("<td><a class=\"navLink\" href=\""+link+"&"+partNumberName+"="+(currentPart+1)+"\">"+(currentPart+2)+"</a></td>");
                    pw.print("<td class=\"commonTD\">...</td>");
                }
            }else{
                if (partsCount>=2){
                    if (currentPart==0){
                        pw.print("<td><a class=\"navSelectedLink\" href=\""+link+"&"+partNumberName+"=0\">1</a></td>");
                    } else {
                        pw.print("<td><a class=\"navLink\" href=\""+link+"&"+partNumberName+"=0\">1</a></td>");
                    }
                    if (currentPart==1){
                        pw.print("<td><a class=\"navSelectedLink\" href=\""+link+"&"+partNumberName+"=1\">2</a></td>");
                    } else {
                        pw.print("<td><a class=\"navLink\" href=\""+link+"&"+partNumberName+"=1\">2</a></td>");
                    }
                }
                if (partsCount>=3){
                    if (currentPart==2){
                        pw.print("<td><a class=\"navSelectedLink\" href=\""+link+"&"+partNumberName+"=2\">3</a></td>");
                    } else {
                        pw.print("<td><a class=\"navLink\" href=\""+link+"&"+partNumberName+"=2\">3</a></td>");
                    }
                }
                if (partsCount>=4){
                    if (currentPart==3){
                        pw.print("<td><a class=\"navSelectedLink\" href=\""+link+"&"+partNumberName+"=3\">4</a></td>");
                    } else {
                        pw.print("<td><a class=\"navLink\" href=\""+link+"&"+partNumberName+"=3\">4</a></td>");
                    }
                }
            }
            if (partsCount>4){
                if (currentPart<partsCount-1){
                    pw.print("<td><a href=\""+link+"&"+partNumberName+"="+(currentPart+1)+"\"><image src=\"images/button.right.gif\" " +
                            " border=0 width=\"24\" height=\"24\"></a></td>");
                    pw.print("<td><a href=\""+link+"&"+partNumberName+"="+(partsCount-1)+"\"><image src=\"images/button.right.end.gif\" " +
                            " border=0 width=\"24\" height=\"24\"></a></td>");
                }
            }
            pw.print("</tr></table>");
        }
    }
}
