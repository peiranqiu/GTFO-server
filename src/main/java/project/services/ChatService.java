package project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import project.models.Message;
import project.models.Chat;
import project.models.User;
import project.repositories.MessageRepository;
import project.repositories.ChatRepository;
import project.repositories.UserRepository;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
public class ChatService {
  @Autowired
  ChatRepository chatRepository;
  @Autowired
  UserRepository userRepository;
  @Autowired
  MessageRepository messageRepository;

  @GetMapping("/api/chat/{chatId}")
  public Chat findChatById(@PathVariable("chatId") int chatId) {
    Optional<Chat> data = chatRepository.findById(chatId);
    if (data.isPresent()) {
      return data.get();
    }
    return null;
  }

  @DeleteMapping("/api/chat/{chatId}")
  public void deleteChat(@PathVariable("chatId") int chatId) {
    chatRepository.deleteById(chatId);
  }


  @PostMapping("/api/chat")
  public Chat createChat(@RequestBody List<User> users) {
    Chat chat = new Chat();
    chat.setUsers(users);
    chat.setSize(users.size());
    String name = users.get(0).getUsername();
    for(int i = 1; i < users.size(); i++) {
      name = name + ", " + users.get(i).getUsername();
    }
    chat.setName(name);
    return chatRepository.save(chat);
  }

  @PutMapping("/api/chat/{chatId}")
  public Chat updateChat(@PathVariable("chatId") int chatId, @RequestBody Chat newChat) {
    Optional<Chat> data = chatRepository.findById(chatId);

    if (data.isPresent()) {
      Chat chat = data.get();
      chat.setName(newChat.getName());
      chat.setAddress(newChat.getAddress());
      chat.setTime(newChat.getTime());
      chat.setStatus(newChat.isStatus());

      return chatRepository.save(chat);
    }
    return null;
  }

  @PostMapping("/api/chat/{chatId}/message")
  public Message createMessage(@PathVariable("chatId") int chatId, @RequestBody Message newMessage) {
    Optional<Chat> data = chatRepository.findById(chatId);
    if (data.isPresent()) {
      Chat chat = data.get();
      newMessage.setChat(chat);
      return messageRepository.save(newMessage);
    }
    return null;
  }

  @GetMapping("/api/chat/{chatId}/message")
  public List<Message> findMessagesForChat(@PathVariable("chatId") int chatId) {
    Optional<Chat> data = chatRepository.findById(chatId);
    if (data.isPresent()) {
      Chat chat = data.get();
      return chat.getMessages();
    }
    return null;
  }
  @GetMapping("/api/message")
  public List<Message> findAllMessages() {
    return (List<Message>) messageRepository.findAll();
  }

  @GetMapping("/api/chat")
  public List<Chat> findAllChats() {
    return (List<Chat>) chatRepository.findAll();
  }

  @GetMapping("/api/user/{userId}/chat")
  public List<Chat> findChatsForUser(@PathVariable("userId") int userId) {
    Optional<User> data = userRepository.findById(userId);
    if (data.isPresent()) {
      return data.get().getChats();
    }
    return null;
  }
}