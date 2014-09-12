package arta.common.http;


public abstract class HttpHandler {

    /**
     * Метод вызывается при встрече в запросе имени новой переменной
     * @param name название переменой
     * @param fileName название файла (присутствует только если переменная является файлом)
     * @param mimeType миме тип файл (присутствует только если переменная является файлом)
     * @param b массив байт содержащих начало значения данной переменной
     */
    public abstract void startData(String name, String fileName, String mimeType, byte[] b);

    /**
     * Метод вызывается при дополнении значения переменной
     * @param name - название переменной
     * @param b - массив байт - очередная часть значения переменной
     */
    public abstract void appendData(String name, byte[] b);

    /**
     * Метод вызывается когда значение переменной считано полностью
     * @param name - название переменной
     */
    public abstract void endData(String name);

    /**
     * В этом методе дожлны содержаться действия, которые необходимо выполнить
     * при завершении загрузки данных. Не вызывается автоматически, необходимо вызвать
     * после окончания работы метода parse() парсера.
     */
    public abstract void requestParsed();

}
