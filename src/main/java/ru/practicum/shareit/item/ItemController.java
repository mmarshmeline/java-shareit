package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

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
    public ResponseEntity<ItemDto> addItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        log.info("Добавлен новый товар.");
        return new ResponseEntity<>(itemService.addItem(userId, itemDto), HttpStatus.OK);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> editItem(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable Long itemId,
                                            @RequestBody ItemDto itemDto) {
        log.info("Информация о товаре c id " + itemId + " отредактирована.");
        return new ResponseEntity<>(itemService.editItem(ownerId, itemId, itemDto), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable Long itemId) {
        log.info("Предоставляем инфо о запрошенном вами товаре с id " + itemId);
        return new ResponseEntity<>(itemService.getItem(itemId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> readUsersItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Предоставляем инфо о товарах пользователя с id " + ownerId);
        return new ResponseEntity<>(itemService.readUsersItems(ownerId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> readFoundItems(@RequestParam String text) {
        log.info("Предоставляем инфо о найденных товарах по запросу " + text);
        return new ResponseEntity<>(itemService.readFoundItems(text), HttpStatus.OK);
    }
}
