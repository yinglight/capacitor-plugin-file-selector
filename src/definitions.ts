declare module '@capacitor/core' {
  interface PluginRegistry {
    FileSelector: FileSelectorPlugin;
  }
}

export interface FileSelectorPlugin {
  chooser(options: { multiple: boolean, max: number }): Promise<any>;
}
