package utils.exceptions;
/*
Специальное исключение для случаев поиска эпиков при создании SubTask.
Необходимо, чтобы корректно работало API при несуществующих эпиках.
 */
public class NotFoundEpicException extends NotFoundException {
    public NotFoundEpicException(String message) {
        super(message);
    }
}
