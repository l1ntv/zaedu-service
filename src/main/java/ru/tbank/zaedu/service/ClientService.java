package ru.tbank.zaedu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.zaedu.DTO.ClientProfileDTO;
import ru.tbank.zaedu.DTO.OrderDTO;
import ru.tbank.zaedu.repo.ClientProfileRepository;
import ru.tbank.zaedu.models.ClientProfile;
import ru.tbank.zaedu.repo.OrderRepository;
import ru.tbank.zaedu.models.Order;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientProfileRepository clientProfileRepository;
    private final OrderRepository orderRepository;

    public Optional<ClientProfileDTO> getClientProfileById(long clientId) {
        Optional<ClientProfile> clientProfileOptional = clientProfileRepository.findById(clientId);
        return clientProfileOptional.map(this::convertToClientProfileDTO);
    }

    public List<OrderDTO> getClientOrders(long clientId) {
        List<Order> orders = orderRepository.findByClientId(clientId);
        return orders.stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());
    }

    private ClientProfileDTO convertToClientProfileDTO(ClientProfile clientProfile) {
        ClientProfileDTO dto = new ClientProfileDTO();
        dto.setId(clientProfile.getId());
        dto.setName(clientProfile.getName());
        dto.setSurname(clientProfile.getSurname());
        dto.setPatronymic(clientProfile.getPatronymic());
        dto.setEmail(clientProfile.getEmail());
        dto.setTelephoneNumber(clientProfile.getTelephoneNumber());
        dto.setCityId(clientProfile.getCity().getId());
        dto.setCityName(clientProfile.getCity().getName());
        if (clientProfile.getUser() != null) {
            dto.setUserId(clientProfile.getUser().getId());
        } else {
            dto.setUserId(null);
        }
        return dto;
    }


    private OrderDTO convertToOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setClientId(order.getClient().getId());
        dto.setMasterId(order.getMaster().getId());
        dto.setClientName(order.getClient().getName() + " " + order.getClient().getSurname());
        dto.setMasterName(order.getMaster().getName() + " " + order.getMaster().getSurname());
        dto.setDescription(order.getDescription());
        dto.setAddress(order.getAddress());
        dto.setPrice(order.getPrice());
        dto.setDateFrom(order.getDateFrom());
        dto.setDateTo(order.getDateTo());
        dto.setOrderStatus(order.getStatus().getName());
        return dto;
    }
}
