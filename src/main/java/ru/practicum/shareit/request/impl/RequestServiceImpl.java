package ru.practicum.shareit.request.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestIncomeDto;
import ru.practicum.shareit.request.dto.ItemRequestLongDto;
import ru.practicum.shareit.request.exception.EntityNotExistException;
import ru.practicum.shareit.request.exception.RequestException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
    public ItemRequestDto create(ItemRequestIncomeDto requestDto, long userId) {
        User requestor = findUserById(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestDto, requestor);

        itemRequest = itemRequestRepository.save(itemRequest);
        log.info("Создан запрос c id = {} ", itemRequest.getId());

        return ItemRequestMapper.toItemRequestDto(itemRequest);
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
            throw new RequestException("Не возвращен запрос c id =  " + userId);
        }
        return ItemRequestMapper.toItemRequestDtoForOwner(itemRequest, itemsByRequest);
    }

    @Override
    public List<ItemRequestLongDto> getForOwner(long userId) {
        findUserById(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_Id(userId, sort);
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
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Запрос c id = %s не существует", requestId))
                );
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotExistException(
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
