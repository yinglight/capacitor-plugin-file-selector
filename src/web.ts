import { WebPlugin } from '@capacitor/core';
import { FileSelectorPlugin } from './definitions';

export class FileSelectorWeb extends WebPlugin implements FileSelectorPlugin {
  constructor() {
    super({
      name: 'FileSelector',
      platforms: ['web'],
    });
  }

  async chooser(options: { multiple: boolean, max: number }): Promise<any> {
    console.log('ECHO', options);
    return options;
  }
}

const FileSelector = new FileSelectorWeb();

export { FileSelector };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(FileSelector);
