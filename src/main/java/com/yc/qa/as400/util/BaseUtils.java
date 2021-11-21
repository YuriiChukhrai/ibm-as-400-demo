package com.yc.qa.as400.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.*;

import com.yc.qa.as400.TerminalDriver;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j;

/**
 *
 * @author limit (Yurii Chukhrai)
 */
@Log4j
public final class BaseUtils {

	private BaseUtils() {
		throw new UnsupportedOperationException("Illegal access to private constructor");
	}

	@Step("Make screenshot [{0}] for JFrame")
	public static void makeScreenCapture(final String name, final TerminalDriver driver) {

		if (Objects.nonNull(driver)) {
			JFrame frame = driver.getFrame();
			if (Objects.nonNull(frame) && frame.isVisible()) {
				makeScreenCapture(String.format("%s-%s", BaseUtils.getTimeFormat("H:m:s"), name), frame);
			} else {
				log.warn(String.format("TID [%d] Can't create screenshot. Visible of frame [%s]", Thread.currentThread().getId(), Objects.nonNull(frame) ? frame.isVisible() : "N/A"));
				log.info(String.format("TID [%d] Can't create screenshot. Terminal driver [%s], visible of frame [%s]", Thread.currentThread().getId(), driver, Objects.nonNull(frame) ? frame.isVisible() : "N/A"));
			}
		} else {
			log.error(String.format("TID [%d] Terminal driver was [NULL]. Screenshot name [%s]", Thread.currentThread().getId(), name));
		}
	}

	@Attachment(value = "{0}", type = "image/png")
	public static byte[] makeScreenCapture(final String name, final JFrame frame) {
		final Rectangle recTangle = frame.getBounds();
		final BufferedImage bufferedImage = new BufferedImage(recTangle.width, recTangle.height, BufferedImage.TYPE_INT_RGB);
		// TYPE_3BYTE_BGR
		frame.paint(bufferedImage.getGraphics());

		try (ByteArrayOutputStream buff = new ByteArrayOutputStream()) {
			ImageIO.write(bufferedImage, "jpg", buff);
			log.info(String.format("TID [%d] Made screenshot for [%s]", Thread.currentThread().getId(), name));
			return buff.toByteArray();
		} catch (IOException e) {
			log.error(String.format("TID [%d] Can't make screenshot for [%s]. Msg [%s].",
					Thread.currentThread().getId(), name, e.getMessage()));
			e.printStackTrace();
		}

		return null;
	}

	/**
	 *
	 * Patterns: 1) "HH:mm:ss" - 24 Patterns: 2) "hh:mm:ss a" - 12
	 *
	 */
	@Step("Return current time in format [{0}]")
	public static String getTimeFormat(final String pattern) {
		final LocalTime ltNow = LocalTime.now();
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return ltNow.format(formatter);
	}

	/* This method make Text attachment for Allure report */
	@Attachment(value = "{0}", type = "text/plain")
	public static synchronized String attachText(final String nameOfAttachment, final String bodyOfMessage) {

		log.info(String.format("TID [%d] - Attached to allure file [%s].", Thread.currentThread().getId(), nameOfAttachment));

		return bodyOfMessage;
	}

	@Step("Check if String [{0}] is empty")
	public static boolean isEmpty(final String s) {
		return Objects.isNull(s) || s.isEmpty();
	}


	@Step("Joining collection to the String")
	public static synchronized String concatCollectionToString(final List<String> list, boolean isNeedSort) {

		if (Objects.nonNull(list) && !list.isEmpty()) {

			if (isNeedSort) {
				// Sort List before concat
				Collections.sort(list);
				log.info(String.format("TID [%1$d] Collection will be sorted. Size [%2$s]", Thread.currentThread().getId(), list.size()));
			}
			return String.join("\n", list);
		}
		return null;
	}

	/**
	 *This method return Certain string from input.
	 * Example:
	 * @param patternRegex ->
	 * @param input ->
	 * @param groupNumber ->
	 *
	 *
	 *
	 * Verification:
	 * 	1) http://regexr.com/
	 * 	2) http://www.regular-expressions.info/refadv.html
	 * 	3) http://www.rexegg.com/regex-lookarounds.html
	 */
	public static String regExp(final String patternRegex, final String input, int groupNumber) {

		String found = null;
		final String cleanInput = Objects.nonNull(input) ? input.replaceAll(Constants.CONTROL_CHARACTERS_REGEX, " ") : null;

		final Pattern pattern = Pattern.compile(patternRegex, Pattern.CASE_INSENSITIVE);
		final Matcher regex = pattern.matcher(cleanInput);

		if (Objects.nonNull(cleanInput) && regex.find()) {
			if (regex.groupCount() >= groupNumber - 1) {
				found = regex.group(groupNumber);
				log.info(String.format("TID [%d] Was found [%s] in line [%s]. Regex [%s]", Thread.currentThread().getId(), found, cleanInput.substring(0, 20), patternRegex));
			} else {
				log.warn(String.format("TID [%d] Regex group NOT correct [%d]. Was selected the last group", Thread.currentThread().getId(), groupNumber));
				found = regex.group(regex.groupCount());
			}
		} else {
			log.info(String.format("TID [%d] Can't find pattern [%s] in [%s]", Thread.currentThread().getId(), patternRegex, cleanInput == null ? "null" : cleanInput.substring(0, 20)));
		}

		return found;
	}

	public static String replaceStringRegex(final String regex, final String sourceValue, final String targetValue) {
		return Objects.nonNull(sourceValue) ? sourceValue.replaceAll(Constants.CONTROL_CHARACTERS_REGEX, "").trim().replaceAll(regex, targetValue) : null;
	}

	public static void handleClosingTerminal() {
		if (Objects.nonNull(ThreadLocalStore.getTerminalDriverAs400()) && ThreadLocalStore.getTerminalDriverAs400().isTerminalConnected()) {
			ThreadLocalStore.getTerminalDriverAs400().closeTN5250J();
		}
		ThreadLocalStore.getTerminalDriverAs400Container().remove();
	}
}//BaseUtils
