package com.example.tasklist.service.impl;

import com.example.tasklist.domain.exception.ResourceNotFoundException;
import com.example.tasklist.domain.task.Status;
import com.example.tasklist.domain.task.Task;
import com.example.tasklist.domain.task.TaskImage;
import com.example.tasklist.domain.user.User;
import com.example.tasklist.repository.TaskRepository;
import com.example.tasklist.service.ImageService;
import com.example.tasklist.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    public void getTaskByValidId_ShouldReturnValidTask() {
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        Task testTask = taskService.getById(taskId);
        verify(taskRepository).findById(taskId);
        assertEquals(task, testTask);
    }

    @Test
    public void getTask_WithNotValidId_ShouldThrowResourceNotFoundException() {
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> taskService.getById(taskId));
        verify(taskRepository).findById(taskId);
    }

    @Test
    public void getAllTasks_WithValidUserId_ShouldReturnListOfTasks() {
        Long userId = 1L;
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            tasks.add(new Task());
        }
        when(taskRepository.findAllByUserId(userId)).thenReturn(tasks);
        List<Task> testTasks = taskService.getAllByUserId(userId);
        assertEquals(tasks, testTasks);
        verify(taskRepository).findAllByUserId(userId);
    }

    @Test
    public void getAllTasks_WithNotValidUserId_ShouldThrowResourceNotFoundException() {
        Long invalidUserId = 111L;
        when(taskRepository.findAllByUserId(invalidUserId)).thenReturn(Collections.emptyList());

        List<Task> testTasks = taskService.getAllByUserId(invalidUserId);

        assertTrue(testTasks.isEmpty(), "Список задач должен быть пустым");

        when(taskRepository.findById(2L)).thenThrow(new ResourceNotFoundException("Task not found."));

        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.getById(2L);
        });

        verify(taskRepository).findAllByUserId(invalidUserId);
    }

    @Test
    public void updateTask_ShouldReturnUpdatedTask_OK() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Title");
        task.setDescription("Descriptions");
        task.setStatus(Status.DONE);
        task.setExpirationDate(LocalDateTime.now());
        Task updatedTask = taskService.update(task);
        verify(taskRepository).save(task);
        assertEquals(task, updatedTask);
    }

    @Test
    public void updateTask_WithNullStatus_ShouldReturnUpdatedTask_WithStatusTODO() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Title");
        task.setDescription("Descriptions");
        task.setExpirationDate(LocalDateTime.now());
        Task updatedTask = taskService.update(task);
        verify(taskRepository).save(task);
        assertEquals(updatedTask.getStatus(), Status.TODO);
    }

    @Test
    public void createTask_WithValidUserId_ShouldReturnTask() {
        Long userId = 1L;
        Long taskId = 1L;

        Task task = new Task();
        task.setId(taskId);

        User user = new User();
        user.setId(userId);
        user.setTasks(new ArrayList<>());

        when(userService.getById(userId)).thenReturn(user);

        Task createdTask = taskService.create(task, userId);

        assertEquals(Status.TODO, createdTask.getStatus(),
                "Статус задачи должен быть TODO");

        assertTrue(user.getTasks().contains(createdTask),
                "Задача должна быть добавлена в список пользователя");

        assertNotNull(createdTask.getId(), "Задаче должен быть присвоен ID");

        verify(userService).update(user);
    }

    @Test
    public void deleteTask_WithValidTaskId() {
        Long taskId = 1L;
        taskService.delete(taskId);
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    public void uploadImage() {
        Long taskId = 1L;
        TaskImage taskImage = new TaskImage();
        String fileName = "uploaded-image.png";

        Task task = new Task();
        task.setId(taskId);
        task.setImages(new ArrayList<>());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        when(imageService.upload(taskImage)).thenReturn(fileName);

        taskService.uploadImage(taskId, taskImage);

        assertTrue(task.getImages().contains(fileName),
                "Изображение должно быть добавлено в список задач");

        verify(taskRepository).save(task);
    }
}