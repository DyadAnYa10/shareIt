package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestor_Id(long requestorId, Sort sort);

    @Query("select ir from ItemRequest as ir where ir.requestor.id not in ?1")
    Page<ItemRequest> findAllForeign(long ownerId, Pageable pageable);
}
