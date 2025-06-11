package org.example.expert.domain.todo.service;

import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private WeatherClient weatherClient;

    @InjectMocks
    private TodoService todoService;

    private AuthUser authUser;
    private TodoSaveRequest todoSaveRequest;

    void loginSetup() {
        this.authUser = new AuthUser(1L, "test@email.com", UserRole.of("USER"));
        this.todoSaveRequest = new TodoSaveRequest("title", "contents");
    }

    @Test
    @DisplayName("게시글 저장 테스트 (성공)")
    void 게시글_저장_성공_검증() {
        loginSetup();

        User user = User.fromAuthUser(this.authUser);

        String mockWeather = "good";
        given(weatherClient.getTodayWeather()).willReturn(mockWeather);

        Todo todo = new Todo(
            this.todoSaveRequest.getTitle(),
            this.todoSaveRequest.getContents(),
            mockWeather,
            user
        );

        given(todoRepository.save(any(Todo.class))).willReturn(todo);

        // when
        TodoSaveResponse response = todoService.saveTodo(this.authUser, this.todoSaveRequest);

        // then
        Assertions.assertThat(response.getTitle()).isEqualTo(todo.getTitle());
        Assertions.assertThat(response.getContents()).isEqualTo(todo.getContents());

        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    @DisplayName("게시글 단일 조회 (실패)")
    void 게시글_단일_조회_실패_검증() {
        // given
        Long todoId = 1L;
        given(todoRepository.findWithUserById(anyLong())).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            todoService.getTodo(todoId);
        });

        // then
        Assertions.assertThat(exception.getMessage()).isEqualTo("Todo not found");
    }

    @Test
    @DisplayName("게시글 전체 조회 (성공)")
    void 게시글_전체_조회_성공_검증() {
        // given
        int page = 1;
        int size = 10;

        User user = new User("test@naver.com", "password", UserRole.of("USER"));
        Todo firstTodo = new Todo("todo1", "todo1", "good", user);
        Todo secondTodo = new Todo("todo2", "todo2", "good", user);

        List<Todo> todos = List.of(firstTodo, secondTodo);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Todo> todoPage = new PageImpl<>(todos, pageable, todos.size());

        given(todoRepository.findAllByOrderByModifiedAtDesc(any(Pageable.class))).willReturn(todoPage);

        // when

        Page<TodoResponse> response = todoService.getTodos(page,size);

        // then

        Assertions.assertThat(response.getContent().get(0).getTitle()).isEqualTo("todo1");
        Assertions.assertThat(response.getContent().get(1).getTitle()).isEqualTo("todo2");

        verify(todoRepository, times(1)).findAllByOrderByModifiedAtDesc(any(Pageable.class));
    }

    @Test
    @DisplayName("게시글 수정 테스트 (실패)")
    void 게시글_수정_실패_검증() {

        // given
        loginSetup();
        Long todoId = 1L;

        given(todoRepository.findById(anyLong())).willReturn(Optional.empty());
        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            todoService.updateTodo(this.authUser, this.todoSaveRequest, todoId);
        });

        // then
        Assertions.assertThat(exception.getMessage()).isEqualTo("Todo not found");
    }

    @Test
    @DisplayName("게시글 수정 테스트 (성공)")
    void 게시글_수정_성공_검증() {
        loginSetup();
        Long todoId = 1L;

        User user = User.fromAuthUser(this.authUser);
        String mockWeather = "good";
        Todo todo = new Todo(this.todoSaveRequest.getTitle(), this.todoSaveRequest.getContents(), mockWeather, user);

        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));

        // when
        TodoResponse response = todoService.updateTodo(authUser, this.todoSaveRequest, todoId);

        // then
        Assertions.assertThat(response.getTitle()).isEqualTo(this.todoSaveRequest.getTitle());
        Assertions.assertThat(response.getContents()).isEqualTo(this.todoSaveRequest.getContents());

        verify(todoRepository, times(1)).findById(anyLong());
    }
}
