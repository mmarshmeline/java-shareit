package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemDtoResponse> addItem(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                   @Valid @RequestBody ItemDto itemDto) {
        log.info("Пользователь с id " + userId + " добавил новый товар.");
        return new ResponseEntity<>(itemService.addItem(itemDto, userId), HttpStatus.CREATED);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<ItemDtoResponse> updateItem(@RequestHeader("X-Sharer-User-Id") @Min(1) Long ownerId,
                                                      @RequestBody ItemDtoUpdate itemDtoUpdate,
                                                      @PathVariable Long itemId) {
        log.info("Редактируем инфо о товаре ...");
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.updateItem(itemId, ownerId, itemDtoUpdate));
    }

    @GetMapping("{itemId}")
    public ResponseEntity<ItemDtoResponse> getItem(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                   @PathVariable Long itemId) {
        log.info("Предоставляем инфо о запрошенном вами товаре с id " + itemId + "...");
        return new ResponseEntity<>(itemService.getItem(userId, itemId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ItemListDto> readUsersItem(@RequestHeader("X-Sharer-User-Id") @Min(1) Long ownerId) {
        log.info("Предоставляем инфо о товарах пользователя с id " + ownerId + "...");
        return new ResponseEntity<>(itemService.readUsersItems(ownerId), HttpStatus.OK);
    }

    @GetMapping("search")
    public ResponseEntity<ItemListDto> readFoundItems(@RequestParam String text) {
        log.info("Предоставляем инфо о найденных товарах по запросу " + text + "...");
        return new ResponseEntity<>(itemService.readFoundItems(text), HttpStatus.OK);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<CommentDtoResponse> addComment(@PathVariable @Min(1) Long itemId,
                                                         @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                         @Valid @RequestBody CommentDto commentDto) {
        log.info("Пользователь с id " + userId + " оставил отзыв к товару с id " + itemId + ".");
        return new ResponseEntity<>(itemService.addComment(itemId, userId, commentDto), HttpStatus.OK);
    }
}