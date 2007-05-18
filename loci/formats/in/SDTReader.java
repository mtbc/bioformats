//
// SDTReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.awt.image.BufferedImage;
import java.io.*;
import loci.formats.*;

/**
 * SDTReader is the file format reader for
 * Becker &amp; Hickl SPC-Image SDT files.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class SDTReader extends FormatReader {

  // -- Fields --

  /** Object containing SDT header information. */
  protected SDTInfo info;

  /** Offset to binary data. */
  protected int off;

  /** Number of time bins in lifetime histogram. */
  protected int timeBins;

  /** Number of spectral channels. */
  protected int channels;

  /** Whether to combine lifetime bins into single intensity image planes. */
  protected boolean intensity = true;

  // -- Constructor --

  /** Constructs a new SDT reader. */
  public SDTReader() { super("SPCImage Data", "sdt"); }

  // -- SDTReader API methods --

  /**
   * Toggles whether the reader should return intensity
   * data only (the sum of each lifetime histogram).
   */
  public void setIntensity(boolean intensity) {
    FormatTools.assertId(currentId, false, 1);
    this.intensity = intensity;
  }

  /**
   * Gets whether the reader is combining each lifetime
   * histogram into a summed intensity image plane.
   */
  public boolean isIntensity() { return intensity; }

  /** Gets the number of bins in the lifetime histogram. */
  public int getTimeBinCount() {
    return timeBins;
  }

  /** Gets the number of spectral channels. */
  public int getChannelCount() {
    return channels;
  }

  /** Gets object containing SDT header information. */
  public SDTInfo getInfo() {
    return info;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) { return false; }

  /* @see loci.formats.IFormatReader#getRGBChannelCount(String) */
  public int getRGBChannelCount() {
    FormatTools.assertId(currentId, true, 1);
    return intensity ? 1 : timeBins;
  }

  /* @see loci.formats.IFormatReader#getChannelDimLengths() */
  public int[] getChannelDimLengths() {
    FormatTools.assertId(currentId, true, 1);
    return intensity ? new int[] {channels} : new int[] {timeBins, channels};
  }

  /* @see loci.formats.IFormatReader#getChannelDimTypes() */
  public String[] getChannelDimTypes() {
    FormatTools.assertId(currentId, true, 1);
    return intensity ? new String[] {FormatTools.SPECTRA} :
      new String[] {FormatTools.LIFETIME, FormatTools.SPECTRA};
  }

  /* @see loci.formats.IFormatReader#isInterleaved(int) */
  public boolean isInterleaved(int subC) {
    FormatTools.assertId(currentId, true, 1);
    return !intensity && subC == 0;
  }

  /* @see loci.formats.IFormatReader#openBytes(int) */
  public byte[] openBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    int c = getRGBChannelCount();
    byte[] buf = new byte[2 * c * core.sizeX[series] * core.sizeY[series]];
    return openBytes(no, buf);
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    if (no < 0 || no >= timeBins * channels) {
      throw new FormatException("Invalid image number: " + no);
    }
    int c = getRGBChannelCount();
    if (buf.length < 2 * c * core.sizeX[series] * core.sizeY[series]) {
      throw new FormatException("Buffer too small");
    }

    if (intensity) {
      in.seek(off + 2 * core.sizeX[series] * core.sizeY[series] *
        timeBins * no);
      for (int y=0; y<core.sizeY[series]; y++) {
        for (int x=0; x<core.sizeX[series]; x++) {
          // read all lifetime bins at this pixel for this channel

          // combine lifetime bins into intensity value
          short sum = 0;
          for (int t=0; t<timeBins; t++) {
            sum += DataTools.read2SignedBytes(in, true);
          }
          int ndx = 2 * (core.sizeX[0] * y + x);
          buf[ndx] = (byte) (sum & 0xff);
          buf[ndx + 1] = (byte) ((sum >> 8) & 0xff);
        }
      }
    }
    else {
      in.seek(off + 2 * core.sizeX[series]*core.sizeY[series] * timeBins * no);
      for (int y=0; y<core.sizeY[series]; y++) {
        for (int x=0; x<core.sizeX[series]; x++) {
          for (int t=0; t<timeBins; t++) {
            int ndx = 2 * (timeBins * core.sizeX[0] * y + timeBins * x + t);
            in.readFully(buf, ndx, 2);
          }
        }
      }
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return ImageTools.makeImage(openBytes(no), core.sizeX[series],
      core.sizeY[series], getRGBChannelCount(), false, 2, true);
  }

  // -- Internal FormatReader API methods --

  /** Initializes the given SDT file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("SDTReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);
    in.order(true);

    status("Reading header");

    // read file header information
    info = new SDTInfo(in, metadata);
    off = info.dataBlockOffs + 22;
    timeBins = info.timeBins;
    channels = info.channels;
    addMeta("time bins", new Integer(timeBins));
    addMeta("channels", new Integer(channels));

    status("Populating metadata");

    core.sizeX[0] = info.width;
    core.sizeY[0] = info.height;
    core.sizeZ[0] = 1;
    core.sizeC[0] = intensity ? channels : timeBins * channels;
    core.sizeT[0] = 1;
    core.currentOrder[0] = "XYZTC";
    core.pixelType[0] = FormatTools.UINT16;
    core.rgb[0] = getRGBChannelCount() > 1;
    core.littleEndian[0] = true;
    core.imageCount[0] = channels;

    MetadataStore store = getMetadataStore();
    store.setPixels(new Integer(core.sizeX[0]), new Integer(core.sizeY[0]),
      new Integer(core.sizeZ[0]), new Integer(core.sizeC[0]),
      new Integer(core.sizeT[0]), new Integer(core.pixelType[0]),
      new Boolean(!isLittleEndian()), core.currentOrder[0], null, null);
    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null);
    }
  }

  // -- Deprecated API methods --

  /** @deprecated Replaced by {@link #getTimeBinCount()} */
  public int getTimeBinCount(String id) throws FormatException, IOException {
    setId(id);
    return getTimeBinCount();
  }

  /** @deprecated Replaced by {@link #getChannelCount()} */
  public int getChannelCount(String id) throws FormatException, IOException {
    setId(id);
    return getChannelCount();
  }

  /** @deprecated Replaced by {@link #getInfo()} */
  public SDTInfo getInfo(String id) throws FormatException, IOException {
    setId(id);
    return getInfo();
  }

}
