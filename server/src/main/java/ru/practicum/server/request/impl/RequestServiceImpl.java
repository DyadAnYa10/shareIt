package ru.practicum.server.request.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.request.ItemRequestRepository;
import ru.practicum.server.request.RequestService;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.dto.ItemRequestLongDto;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto create(ItemRequestDto requestDto, long userId) {
        findUserById(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestorId(userId);
        ItemRequest request = itemRequestRepository.save(itemRequest);
        log.info("Создан запрос c id = {} ", itemRequest.getId());

        return ItemRequestMapper.toItemRequestDto(request);
    }

    @Override
    public ItemRequestLongDto getById(long requestId, long userId) {
        ItemRequest itemRequest;
        List<Item> itemsByRequest;
        try {
            findUserById(userId);
            itemRequest = findById(requestId);
            itemsByRequest = findItemsByRequest(itemRequest);

            log.info("Возвращен запрос c id = {} ", userId);
        } catch (Exception e) {
            throw new NotFoundException("Не возвращен запрос c id =  " + userId);
        }
        return ItemRequestMapper.toItemRequestDtoForOwner(itemRequest, itemsByRequest);
    }

    @Override
    public List<ItemRequestLongDto> getForOwner(long userId) {
        findUserById(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(userId, sort);
        Map<ItemRequest, List<Item>> itemsByRequests = findItemsByRequests(itemRequests);

        log.info("Возвращена коллекция запросов на бронирование владельца id = {} ", userId);
        return ItemRequestMapper.toItemRequestDtoForOwner(itemRequests, itemsByRequests);
    }

    @Override
    public List<ItemRequestLongDto> getAll(int from, int size, long userId) {
        findUserById(userId);

        Pageable pageable = PageRequest.of(
                from == 0 ? 0 : (from / size),
                size,
                Sort.by(Sort.Direction.DESC, "created")
        );
        List<ItemRequest> itemRequests = itemRequestRepository.findAllForeign(userId, pageable)
                .toList();
        Map<ItemRequest, List<Item>> itemsByRequests = findItemsByRequests(itemRequests);

        log.info("Возвращена коллекция запросов на бронирование");
        return ItemRequestMapper.toItemRequestDtoForOwner(itemRequests, itemsByRequests);
    }

    private ItemRequest findById(long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Запрос c id = %s не существует", requestId))
                );
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь c id = %s не существует", userId))
                );
    }

    private Map<ItemRequest, List<Item>> findItemsByRequests(List<ItemRequest> requests) {
        return itemRepository.findAllByRequestIdIn(requests)
                .stream()
                .collect(Collectors.groupingBy(Item::getItemRequest, Collectors.toList()));
    }

    private List<Item> findItemsByRequest(ItemRequest request) {
        return itemRepository.findAllByItemRequest(request);
    }
}
