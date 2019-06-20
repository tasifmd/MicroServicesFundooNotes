package com.bridgelabz.note.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bridgelabz.exception.LabelException;
import com.bridgelabz.exception.NoteException;
import com.bridgelabz.note.dto.LabelDto;
import com.bridgelabz.note.model.Label;
import com.bridgelabz.note.model.Note;
import com.bridgelabz.note.repository.LabelRepository;
import com.bridgelabz.note.repository.NoteRepository;
import com.bridgelabz.response.Response;
import com.bridgelabz.util.StatusHelper;

@Service("labelService")
@PropertySource("classpath:message.properties")
@PropertySource("classpath:error.properties")
public class LabelServiceImpl implements ILabelService {

	private Logger logger = LoggerFactory.getLogger(LabelServiceImpl.class);

	@Autowired
	Environment environment;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private LabelRepository labelRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bridgelabz.note.service.ILabelService#createLabel(long,
	 * com.bridgelabz.note.dto.LabelDto)
	 */
	@Transactional
	@Override
	public Response createLabel(long userId, LabelDto labelDto) {
		Label label = modelMapper.map(labelDto, Label.class);
		label.setUserId(userId);
		label.setCreatedDate(LocalDate.now());
		labelRepository.save(label);
		logger.info("Label is successfully created {}", label);
		Response response = StatusHelper.statusInfo(environment.getProperty("labelCreated"),
				Integer.parseInt(environment.getProperty("successCode")));
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bridgelabz.note.service.ILabelService#updateLabel(long, long,
	 * com.bridgelabz.note.dto.LabelDto)
	 */
	@Override
	@Transactional
	public Response updateLabel(long userId, long labelId, LabelDto labelDto) {
		Optional<Label> label = labelRepository.findByUserIdAndLabelId(userId, labelId);
		if (!label.isPresent()) {
			logger.error("No such label exist {}", label.get());
			throw new LabelException(environment.getProperty("labelNotExist"),
					Integer.parseInt(environment.getProperty("labelExceptionCode")));
		}
		label.get().setLabelName(labelDto.getLabelName());
		label.get().setModifiedDate(LocalDate.now());
		labelRepository.save(label.get());
		logger.info("Label has successfully been updated {}", label.get());
		Response response = StatusHelper.statusInfo(environment.getProperty("labelUpdated"),
				Integer.parseInt(environment.getProperty("successCode")));
		return response;
	}

	@Override
	public List<Label> getLabel(long userId) {
		List<Label> listLabels = labelRepository.findLabelByUserId(userId);
		logger.info("Getting label of user {}", listLabels);
		return listLabels;
	}

	@Override
	@Transactional
	public Response deleteLabel(long userId, long labelId) {
		Optional<Label> label = labelRepository.findByUserIdAndLabelId(userId, labelId);
		if (!label.isPresent()) {
			logger.error("No such label exist {}", label.get());
			throw new LabelException(environment.getProperty("labelNotExist"),
					Integer.parseInt(environment.getProperty("labelExceptionCode")));
		}
		labelRepository.delete(label.get());
		Response response = StatusHelper.statusInfo(environment.getProperty("labelDeleted"),
				Integer.parseInt(environment.getProperty("successCode")));
		return response;
	}

	@Override
	@Transactional
	public Response addLabelToNote(long userId, long labelId, long noteId) {
		Optional<Note> note = noteRepository.findByNoteIdAndUserId(noteId, userId);
		if (!note.isPresent()) {
			logger.error("No such note exist {}", note.get());
			throw new NoteException(environment.getProperty("notNotAvailable"),
					Integer.parseInt(environment.getProperty("noteExceptionCode")));
		}
		Optional<Label> label = labelRepository.findByUserIdAndLabelId(userId, labelId);
		if (!label.isPresent()) {
			logger.error("No such label exist {}", label.get());
			throw new LabelException(environment.getProperty("labelNotExist"),
					Integer.parseInt(environment.getProperty("labelExceptionCode")));
		}
		if (note.get().getLabel().contains(label.get())) {
			logger.error("Label already exist in note {}", note.get());
			throw new LabelException(environment.getProperty("labelAlreadyExist"),
					Integer.parseInt(environment.getProperty("labelExceptionCode")));
		}

		note.get().getLabel().add(label.get());
		label.get().getNotes().add(noteRepository.findByNoteIdAndUserId(noteId, userId).get());
		noteRepository.save(note.get());
		labelRepository.save(label.get());
		
		logger.info("Label has successfully added to note {}", note.get());
		Response response = StatusHelper.statusInfo(environment.getProperty("labelAddedToNote"),
				Integer.parseInt(environment.getProperty("successCode")));
		return response;
	}

	@Override
	@Transactional
	public Response removeLabelFromNote(long userId, long labelId, long noteId) {
		Optional<Note> note = noteRepository.findByNoteIdAndUserId(noteId, userId);
		if (!note.isPresent()) {
			logger.error("No such note exist {}", note.get());
			throw new NoteException(environment.getProperty("notNotAvailable"),
					Integer.parseInt(environment.getProperty("noteExceptionCode")));
		}
		Optional<Label> label = labelRepository.findByUserIdAndLabelId(userId, labelId);
		if (!label.isPresent()) {
			logger.error("No such label exist {}", label.get());
			throw new LabelException(environment.getProperty("labelNotExist"),
					Integer.parseInt(environment.getProperty("labelExceptionCode")));
		}

		note.get().getLabel().remove(label.get());
		label.get().getNotes().remove(noteRepository.findByNoteIdAndUserId(noteId, userId).get());
		noteRepository.save(note.get());
		labelRepository.save(label.get());
		logger.info("Label has successfully removed from note {}", note.get());
		Response response = StatusHelper.statusInfo(environment.getProperty("labelRemovedFromNote"),
				Integer.parseInt(environment.getProperty("successCode")));
		return response;
	}

	@Override
	public List<Label> getLabelOfNote(long userId, long noteId) {
		Optional<Note> note = noteRepository.findByNoteIdAndUserId(noteId, userId);
		if (!note.isPresent()) {
			logger.error("No such note exist {}", note.get());
			throw new NoteException(environment.getProperty("notNotAvailable"),
					Integer.parseInt(environment.getProperty("noteExceptionCode")));
		}
		logger.info("Get labels of note {}", note.get());
		List<Label> label = note.get().getLabel();
		return label;
	}

}
