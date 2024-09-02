package br.ce.wcaquino.taskbackend.controller;

import br.ce.wcaquino.taskbackend.model.Task;
import br.ce.wcaquino.taskbackend.repo.TaskRepo;
import br.ce.wcaquino.taskbackend.utils.ValidationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

public class TaskControllerTest {

    @Mock
    private TaskRepo taskRepo;

    //Injeta o mockito nesse controller
    @InjectMocks
    TaskController controller = new TaskController();

    // Before é executado antes de CADA teste
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldNotSaveTaskWithoutDescription() {
        Task todo = new Task();
        todo.setDueDate(LocalDate.now());
        try {
            controller.save(todo);
            Assert.fail("Não deveria chegar nesse ponto pois deveria ocorrer o throw");
        } catch (ValidationException e) {
            // Verifica se o erro recebido quando o metodo da throw na exception equivale ao erro da mensagem, caso seja o mesmo erro, valida o teste
            Assert.assertEquals("Fill the task description", e.getMessage());
        }
    }

    @Test
    public void shouldNotSaveTaskWithoutDueDate() {
        Task todo = new Task();
        todo.setTask("Bom dia!");
        try {
            controller.save(todo);
            Assert.fail("Não deveria chegar nesse ponto pois deveria ocorrer o throw");
        } catch (ValidationException e) {
            // Verifica se o erro recebido quando o metodo da throw na exception equivale ao erro da mensagem, caso seja o mesmo erro, valida o teste
            Assert.assertEquals("Fill the due date", e.getMessage());
        }
    }

    @Test
    public void shouldNotSaveTaskWithDueDateInPast() {
        Task todo = new Task();
        todo.setDueDate(LocalDate.of(2010, 01, 01));
        todo.setTask("Bom dia!");
        try {
            controller.save(todo);
            Assert.fail("Não deveria chegar nesse ponto pois deveria ocorrer o throw");
        } catch (ValidationException e) {
            // Verifica se o erro recebido quando o metodo da throw na exception equivale ao erro da mensagem, caso seja o mesmo erro, valida o teste
            Assert.assertEquals("Due date must not be in past", e.getMessage());
        }
    }

    @Test
    public void shouldSaveTaskSuccessfully() throws ValidationException {
        Task todo = new Task();
        todo.setDueDate(LocalDate.now());
        todo.setTask("Bom dia!");
        controller.save(todo);

        //Verifica se o objeto taskrepo foi utilizado no metodo salvar
        Mockito.verify(taskRepo).save(todo);
    }
}
