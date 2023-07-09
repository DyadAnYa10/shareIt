package ru.practicum.server.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.server.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorId(long requestorId, Sort sort);

    @Query("select ir from ItemRequest as ir where ir.requestorId not in ?1")
    Page<ItemRequest> findAllForeign(long ownerId, Pageable pageable);
}
