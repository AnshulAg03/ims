package com.mandeep.ims.controller;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.created;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mandeep.ims.dto.AllInvoicesResponseDto;
import com.mandeep.ims.dto.CreateInvoiceDto;
import com.mandeep.ims.dto.CreateInvoiceResponseDto;
import com.mandeep.ims.dto.ErrorResponseDto;
import com.mandeep.ims.entity.Invoice;
import com.mandeep.ims.entity.ItemType;
import com.mandeep.ims.exception.CustomException;
import com.mandeep.ims.service.InvoiceService;

@RestController
@RequestMapping("/invoice")
@CrossOrigin(origins = "*")
public class InvoiceController {

	@Autowired
	private InvoiceService invoiceService;

	@GetMapping
	public ResponseEntity<AllInvoicesResponseDto> getAll() {
		AllInvoicesResponseDto invoices;
		try {
			invoices = invoiceService.getAllInvoices();
			return ok().body(invoices);
		} catch (CustomException e) {
			e.printStackTrace();
			return new ResponseEntity(new ErrorResponseDto(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping
	public ResponseEntity<CreateInvoiceResponseDto> createInvoice(@RequestBody CreateInvoiceDto createInvoiceDto) {
		try {
			CreateInvoiceResponseDto createInvoiceResponseDto = invoiceService.createInvoice(createInvoiceDto);
			return created(null).body(createInvoiceResponseDto);
		} catch (CustomException e) {
			e.printStackTrace();
			return new ResponseEntity(new ErrorResponseDto(e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("download/{id}")
	public ResponseEntity downloadInvoice(@PathVariable("id") int id) {
		try {
			return invoiceService.downloadInvoice(id);
		} catch (CustomException e) {
			e.printStackTrace();
			return new ResponseEntity(new ErrorResponseDto(e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/itemTypes")
	public ResponseEntity<List<ItemType>> getItemTypes() {
		List<ItemType> createInvoiceResponseDto = invoiceService.getItemTypes();
		return ok().body(createInvoiceResponseDto);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Invoice> deleteCustomerById(@PathVariable int id) {
		try {
			invoiceService.deleteInvoiceById(id);
			return noContent().build();
		} catch (CustomException e) {
			e.printStackTrace();
			return new ResponseEntity(new ErrorResponseDto(e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}
}
